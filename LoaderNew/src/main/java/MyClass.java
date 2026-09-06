import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Push proxies.txt → Firebase RTDB node {@code a}.
 *
 * proxies.txt order (from fetch_proxies.py / extract_proxies.js):
 *   line 0 = newest on site (page bottom) → must be top of app list
 *   → assign highest {@code update_at} to earlier lines.
 *
 * Refuse to push if fewer than {@link #MIN_PROXIES} entries.
 */
public class MyClass {

    private static final String FIREBASE_URL = "https://mtprotolist.firebaseio.com/a";
    private static final String PROXY_FILE = "proxies.txt";
    private static final int REQUEST_DELAY_MS = 1500; // ip-api ~45 req/min
    /** Do not wipe/pollute Firebase with empty or broken scrapes. */
    private static final int MIN_PROXIES = 20;
    /** Gap between update_at ranks (ms); app sorts by update_at desc. */
    private static final long UPDATE_AT_STEP_MS = 1000L;

    public static void main(String[] args) {
        System.out.println("--- MTProto Desktop Loader (with Rate Limiting) ---");
        List<ProxyData> proxies = readProxiesFromFile(PROXY_FILE);

        if (proxies.isEmpty()) {
            System.err.println("ABORT: в файле " + PROXY_FILE + " прокси не найдены. Firebase не трогаем.");
            return;
        }
        if (proxies.size() < MIN_PROXIES) {
            System.err.println(
                    "ABORT: найдено " + proxies.size() + " < " + MIN_PROXIES
                            + ". Пустой/битый лист в Firebase не пушим."
            );
            return;
        }

        System.out.println("Найдено прокси в файле: " + proxies.size()
                + " (строка 1 = самые свежие на сайте → максимальный update_at)");
        System.out.println("Примерное время: " + (proxies.size() * REQUEST_DELAY_MS / 1000) + " с.");
        fetchGeoAndPushToFirebase(proxies);
    }

    private static List<ProxyData> readProxiesFromFile(String fileName) {
        List<ProxyData> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            file = new File("../../" + fileName);
            if (!file.exists()) {
                return list;
            }
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
        // Newest first in file → highest update_at (FirebaseProxyStreams: orderBy update_at + reverse)
        long baseUpdateAt = System.currentTimeMillis();

        for (int i = 0; i < proxies.size(); i++) {
            ProxyData p = proxies.get(i);
            long updateAt = baseUpdateAt - (i * UPDATE_AT_STEP_MS);
            try {
                System.out.print("[" + (i + 1) + "/" + proxies.size() + "] " + p.host + " (update_at=" + updateAt + ")... ");

                String geoJson = sendGetRequest(
                        "http://ip-api.com/json/" + p.host
                                + "?fields=status,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as"
                );

                if (geoJson.contains("\"status\":\"success\"")) {
                    pushToFirebase(p, geoJson, updateAt);
                } else if (geoJson.contains("fail") && geoJson.contains("reserved range")) {
                    System.out.println("Пропуск (локальный/reserved IP)");
                } else {
                    System.out.println("Ошибка гео: " + geoJson);
                }

                if (i < proxies.size() - 1) {
                    Thread.sleep(REQUEST_DELAY_MS);
                }
            } catch (Exception e) {
                String msg = e.getMessage() == null ? "" : e.getMessage();
                if (msg.contains("429")) {
                    System.err.println("\nHTTP 429 — пауза 10с...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    System.err.println("\nОшибка " + p.host + ": " + msg);
                }
            }
        }
        System.out.println("\nГотово! Прокси залиты (свежие сверху по update_at).");
    }

    private static void pushToFirebase(ProxyData p, String geoJson, long updateAt) {
        try {
            String fullUrl = String.format(
                    "https://t.me/proxy?server=%s&port=%s&secret=%s",
                    p.host, p.port, p.secret
            );
            String key = md5(fullUrl);

            String city = extract(geoJson, "city");
            String country = extract(geoJson, "country");
            String countryCode = extract(geoJson, "countryCode");
            String lat = extract(geoJson, "lat");
            String lon = extract(geoJson, "lon");

            String firebaseJson = String.format(
                    Locale.US,
                    "{\"host\":\"%s\",\"port\":\"%s\",\"secret\":\"%s\",\"enabled\":true,\"update_at\":%d,"
                            + "\"city\":\"%s\",\"country\":\"%s\",\"code\":\"%s\",\"lat\":%s,\"lon\":%s}",
                    escapeJson(p.host),
                    escapeJson(p.port),
                    escapeJson(p.secret),
                    updateAt,
                    escapeJson(city),
                    escapeJson(country),
                    escapeJson(countryCode),
                    emptyAsNullNumber(lat),
                    emptyAsNullNumber(lon)
            );

            String putUrl = FIREBASE_URL + "/" + key + ".json";
            sendPutRequest(putUrl, firebaseJson);
            System.out.println("OK key=" + key);
        } catch (Exception e) {
            System.err.println("Ошибка Firebase: " + e.getMessage());
        }
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String emptyAsNullNumber(String s) {
        if (s == null || s.isEmpty()) {
            return "0";
        }
        return s;
    }

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
        if (code >= 400) {
            throw new IOException("HTTP error: " + code);
        }
        return readStream(conn.getInputStream());
    }

    private static String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        int code = conn.getResponseCode();
        if (code == 429) {
            throw new IOException("HTTP 429");
        }
        if (code >= 400) {
            throw new IOException("HTTP error: " + code);
        }
        return readStream(conn.getInputStream());
    }

    private static String readStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static class ProxyData {
        final String host, port, secret;

        ProxyData(String h, String p, String s) {
            host = h;
            port = p;
            secret = s;
        }
    }
}
