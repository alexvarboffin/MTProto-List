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

    public static void main(String[] args) {
        System.out.println("--- MTProto Desktop Loader ---");
        List<ProxyData> proxies = readProxiesFromFile(PROXY_FILE);

        if (proxies.isEmpty()) {
            System.out.println("В файле " + PROXY_FILE + " прокси не найдены.");
            return;
        }

        System.out.println("Найдено прокси в файле: " + proxies.size());
        fetchGeoAndPushToFirebase(proxies);
    }

    private static List<ProxyData> readProxiesFromFile(String fileName) {
        List<ProxyData> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            // Пытаемся найти в корне проекта, если запуск не из корня
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
        try {
            // Формируем JSON массив для ip-api batch
            StringBuilder batchJson = new StringBuilder("[");
            for (int i = 0; i < proxies.size(); i++) {
                batchJson.append("\"").append(proxies.get(i).host).append("\"");
                if (i < proxies.size() - 1) batchJson.append(",");
            }
            batchJson.append("]");

            System.out.println("Получение геолокации для " + proxies.size() + " серверов...");
            String response = sendPostRequest(IP_API_BATCH, batchJson.toString());
            
            // Здесь нужен парсинг JSON. Так как мы не используем библиотеки, 
            // для надежности пройдемся по каждому прокси отдельно или предположим структуру.
            // Но лучше для десктоп-скрипта использовать цикл по одному для простоты без GSON.
            
            for (ProxyData p : proxies) {
                // Получаем гео для конкретного IP (для простоты в цикле, если batch сложен без парсера)
                String geoJson = sendGetRequest("http://ip-api.com/json/" + p.host + "?fields=status,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as");
                
                if (geoJson.contains("\"status\":\"success\"")) {
                    System.out.println("Гео получено для: " + p.host);
                    System.out.println("Не удалось получить гео для: " + p + geoJson);
                    //pushToFirebase(p, geoJson);
                } else {
                    System.out.println("Не удалось получить гео для: " + p.host);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pushToFirebase(ProxyData p, String geoJson) {
        try {
            String fullUrl = String.format("https://t.me/proxy?server=%s&port=%s&secret=%s", p.host, p.port, p.secret);
            String key = md5(fullUrl);
            
            // Формируем финальный JSON объект вручную (имитируем структуру MtprotoProxy)
            // Достаем данные из geoJson регулярками (чтобы не тянуть тяжелые парсеры)
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
            System.out.println("Успешно пушнули в Firebase: " + p.host + " (Key: " + key + ")");
        } catch (Exception e) {
            System.err.println("Ошибка при отправке в Firebase: " + e.getMessage());
        }
    }

    // --- Утилиты ---

    private static String extract(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\":\"?([^,\"}]+)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static String sendPostRequest(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return readStream(conn.getInputStream());
    }

    private static String sendPutRequest(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT"); // REST API Firebase использует PUT для записи по ключу
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return readStream(conn.getInputStream());
    }

    private static String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        return readStream(url.openStream());
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
