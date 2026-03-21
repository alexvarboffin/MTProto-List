//package com.walhalla.mtprotolist.manager;
//
//import android.os.Handler;
//import android.widget.Toast;
//
//import com.walhalla.mtprotolist.Config;
//import com.walhalla.mtprotolist.entity.MtprotoProxy;
//import com.walhalla.mtprotolist.utils.QueryFormat;
//import com.walhalla.mtprotolist.webproxy.ProxyInfo;
//import com.walhalla.ui.DLog;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//public class MUtils {
//    private final Handler h;
//    private final Executor executor = Executors.newCachedThreadPool(); // Пул потоков
//
//    public void getProxyType(ProxyInfo info, Callback<ProxyInfo> callback) {
//        executor.execute(() -> {
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(info.proxyUrl)
//                    .build();
//            try {
//                Response response = client.newCall(request).execute();
//                String responseBody = response.body().string();
//
//                if (responseBody.contains("Glype")) {
//                    h.post(() -> {
//                        info.type = "Glype";
//                        callback.onResponse(info);
//                    });
//                }
//                else if (responseBody.contains("PHP-Proxy")) {
//                    h.post(() -> {
//                        info.type = "PHP-Proxy";
//                        callback.onResponse(info);
//                    });
//                }
//                else if (responseBody.contains("PHProxy")) {
//                    h.post(() -> {
//                        info.type = "PHProxy";
//                        callback.onResponse(info);
//                    });
//                }
//                else {
//                    h.post(()->{
//                        info.type = "Other";
//                        callback.onResponse(info);
//                    });
//                }
//            } catch (Exception e) {
//                h.post(()->{
//                    callback.onError(e.getClass().getSimpleName());
//                });
//            }
//        });
//    }
//
//
//    public interface Callback<T> {
//        void onError(String error);
//
//        void onResponse(T serverList);
//    }
//
//    public MUtils(Handler handler) {
//        this.h = handler;
//    }
//
//    private final OkHttpClient client = new OkHttpClient();
//    private final String urlCheckIpBatch = "http://ip-api.com/batch";
//
//    public void getIpInfo(final List<MtprotoProxy> serverList, Callback<List<MtprotoProxy>> callback) {
//        executor.execute(() -> {
//            try {
//                JSONArray jsonArray = QueryFormat.getArray0(serverList);
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                RequestBody requestBody = RequestBody.create(JSON, jsonArray.toString());
//
//                Request request = new Request.Builder()
//                        .url(urlCheckIpBatch)
//                        .post(requestBody)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                if (response.isSuccessful()) {
//                    // Обработка успешного ответа
//                    final JSONArray jsonResponse = new JSONArray(response.body().string());
//                    h.post(() -> {
//                        if (setIpInfo(jsonResponse, serverList)) {
//                            callback.onResponse(serverList);
//                        }
//                    });
//                } else {
//                    // Обработка ошибки
//                    final String errorMessage = response.message();
//                    h.post(() -> callback.onError(errorMessage));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                final String errorMessage = e.getMessage();// Обработка исключений
//                h.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onError(errorMessage);
//                    }
//                });
//            }
//        });
//    }
//
//    public void getIpInfoProxyInfo(
//            final List<ProxyInfo> serverList,
//            Callback<List<ProxyInfo>> callback) {
//        executor.execute(() -> {
//            try {
//                JSONArray jsonArray = QueryFormat.getQueryArray(serverList);
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                RequestBody requestBody = RequestBody.create(JSON, jsonArray.toString());
//
//                Request request = new Request.Builder()
//                        .url(urlCheckIpBatch)
//                        .post(requestBody)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                if (response.isSuccessful()) {
//                    // Обработка успешного ответа
//                    final JSONArray jsonResponse = new JSONArray(response.body().string());
//                    h.post(() -> {
//                        if (setIpInfo1(jsonResponse, serverList)) {
//                            callback.onResponse(serverList);
//                        }
//                    });
//                } else {
//                    // Обработка ошибки
//                    final String errorMessage = response.message();
//                    h.post(() -> callback.onError(errorMessage));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                final String errorMessage = e.getMessage();// Обработка исключений
//                h.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onError(errorMessage);
//                    }
//                });
//            }
//        });
//    }
//
//    private boolean setIpInfo1(JSONArray response, List<ProxyInfo> serverList) {
//        boolean result = false;
//
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                // ip ==> ipInfo.get("query").toString()
//                JSONObject ipInfo = response.getJSONObject(i);
//                DLog.d("@@@@@@@" + ipInfo);
//
//                ProxyInfo server = serverList.get(i);
//                server.setCity(ipInfo.getString(Config.KEY_CITY));
//                server.setRegionName(ipInfo.getString(Config.KEY_REGION_NAME));
//                server.setLat(ipInfo.getDouble(Config.KEY_LAT));
//                server.setLon(ipInfo.getDouble(Config.KEY_LON));
//                server.code = ipInfo.getString("countryCode");
//
//                if (server.ip.length() < 6) {
//                    server.ip = ipInfo.getString("query");
//                }
//
//
//                server.country = ipInfo.getString("country");
//                server.region = ipInfo.getString("region");
//                server.zip = ipInfo.getString("zip");
//
//                server.timezone = ipInfo.getString("timezone");
//                server.isp = ipInfo.getString("isp");
//                server.org = ipInfo.getString("org");
//                server.as = ipInfo.getString("as");
//                result = true;
//            } catch (JSONException e) {
//                result = false;
//                DLog.handleException(e);
//            }
//        }
//        return result;
//    }
//
//
//    public boolean setIpInfo(JSONArray response, List<MtprotoProxy> serverList) {
//        boolean result = false;
//
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                // ip ==> ipInfo.get("query").toString()
//                JSONObject ipInfo = response.getJSONObject(i);
//                DLog.d("@@@@@@@" + ipInfo);
//
//                MtprotoProxy server = serverList.get(i);
//                server.setCity(ipInfo.getString(Config.KEY_CITY));
//                server.setRegionName(ipInfo.getString(Config.KEY_REGION_NAME));
//                server.setLat(ipInfo.getDouble(Config.KEY_LAT));
//                server.setLon(ipInfo.getDouble(Config.KEY_LON));
//                server.code = ipInfo.getString("countryCode");
//
//
//                server.country = ipInfo.getString("country");
//                server.region = ipInfo.getString("region");
//                server.zip = ipInfo.getString("zip");
//
//                server.timezone = ipInfo.getString("timezone");
//                server.isp = ipInfo.getString("isp");
//                server.org = ipInfo.getString("org");
//                server.as = ipInfo.getString("as");
//                result = true;
//            } catch (JSONException e) {
//                result = false;
//                DLog.handleException(e);
//            }
//        }
//        return result;
//    }
//}
