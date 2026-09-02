import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClass {

    private static final String FIREBASE_URL = "https://mtprotolist.firebaseio.com/a";
    private static final String IP_API_BATCH = "http://ip-api.com/batch";
    private static final String PROXY_FILE = "proxies.txt";
    private static final int REQUEST_DELAY_MS = 1500; // Задержка 1.5 сек для обхода 429 Error (max 45 req/min)

    public static void main(String[] args) {
        System.out.println("--- MTProto Desktop Loader (with Rate Limiting) ---");
        List<ProxyData> proxies = readProxiesFromFile(PROXY_FILE);

        if (proxies.isEmpty()) {
            System.out.println("В файле " + PROXY_FILE + " прокси не найдены.");
            return;
        }

        System.out.println("Найдено прокси в файле: " + proxies.size());
        System.out.println("Примерное время выполнения: " + (proxies.size() * REQUEST_DELAY_MS / 1000) + " секунд.");
        fetchGeoAndPushToFirebase(proxies);
    }

    private static List<ProxyData> readProxiesFromFile(String fileName) {
        List<ProxyData> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            file = new File("../../" + fileName); 
            if (!file.exists()) return list;
        }

        Pattern pattern = Pattern.compile("tg://proxy\\?server=([^&]+)&port=(\\d+)&secret=([^&\\s]+)");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    list.add(new ProxyData(m.group(1), m.group(2), m.group(3)));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        return list;
    }

    private static void fetchGeoAndPushToFirebase(List<ProxyData> proxies) {
        for (int i = 0; i < proxies.size(); i++) {
            ProxyData p = proxies.get(i);
            try {
                System.out.print("[" + (i + 1) + "/" + proxies.size() + "] Обработка " + p.host + "... ");
                
                // 1. Получаем гео
                String geoJson = sendGetRequest("http://ip-api.com/json/" + p.host + "?fields=status,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as");
                
                if (geoJson.contains("\"status\":\"success\"")) {
                    // 2. Пушим в Firebase
                    pushToFirebase(p, geoJson);
                } else if (geoJson.contains("fail") && geoJson.contains("reserved range")) {
                    System.out.println("Пропуск (локальный IP или зарезервирован)");
                } else {
                    System.out.println("Ошибка гео: " + geoJson);
                }

                // 3. Таймаут между запросами
                if (i < proxies.size() - 1) {
                    Thread.sleep(REQUEST_DELAY_MS);
                }

            } catch (Exception e) {
                if (e.getMessage().contains("429")) {
                    System.err.println("\nОШИБКА 429: Слишком много запросов. Увеличиваю паузу...");
                    try { Thread.sleep(10000); } catch (InterruptedException ignored) {} // Пауза 10 сек при ошибке
                } else {
                    System.err.println("\nОшибка при обработке " + p.host + ": " + e.getMessage());
                }
            }
        }
        System.out.println("\nГотово! Все прокси обработаны.");
    }

    private static void pushToFirebase(ProxyData p, String geoJson) {
        try {
            String fullUrl = String.format("https://t.me/proxy?server=%s&port=%s&secret=%s", p.host, p.port, p.secret);
            String key = md5(fullUrl);
            
            String city = extract(geoJson, "city");
            String country = extract(geoJson, "country");
            String countryCode = extract(geoJson, "countryCode");
            String lat = extract(geoJson, "lat");
            String lon = extract(geoJson, "lon");

            String firebaseJson = String.format(
                "{\"host\":\"%s\",\"port\":\"%s\",\"secret\":\"%s\",\"enabled\":true,\"update_at\":%d," +
                "\"city\":\"%s\",\"country\":\"%s\",\"code\":\"%s\",\"lat\":%s,\"lon\":%s}",
                p.host, p.port, p.secret, System.currentTimeMillis(),
                city, country, countryCode, lat, lon
            );

            String putUrl = FIREBASE_URL + "/" + key + ".json";
            sendPutRequest(putUrl, firebaseJson);
            System.out.println("OK (Firebase Key: " + key + ")");
        } catch (Exception e) {
            System.err.println("Ошибка Firebase: " + e.getMessage());
        }
    }

    // --- Утилиты ---

    private static String extract(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\":\"?([^,\"}]+)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static String sendPutRequest(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code >= 400) throw new IOException("HTTP error: " + code);
        return readStream(conn.getInputStream());
    }

    private static String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        int code = conn.getResponseCode();
        if (code == 429) throw new IOException("HTTP 429");
        if (code >= 400) throw new IOException("HTTP error: " + code);
        return readStream(conn.getInputStream());
    }

    private static String readStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static class ProxyData {
        String host, port, secret;
        ProxyData(String h, String p, String s) { host = h; port = p; secret = s; }
    }
}
