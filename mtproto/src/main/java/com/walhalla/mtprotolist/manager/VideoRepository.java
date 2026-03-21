//package com.walhalla.mtprotolist.manager;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.walhalla.mtprotolist.Config;
//import com.walhalla.mtprotolist.HashUtils;
//import com.walhalla.mtprotolist.manager.MUtils;
//import com.walhalla.mtprotolist.entity.MtprotoProxy;
//import com.walhalla.mtprotolist.webproxy.ProxyInfo;
//import com.walhalla.ui.DLog;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class VideoRepository {
//    private static final String PROXY_PATTERN = "https://t.me/proxy\\?server=(.*?)&port=(.*?)&secret=(.*)";
//
//
//    private final Callback callback;
//    private final MUtils mUtils;
//
//
//    //private static GetVideo getVideo;
//
//    //public static final int ERROR_WENT_WRONG = R.string.abc_something_went_wrong;
//
//    public interface Callback {
//        void successResult(String result);
//    }
//
//    public VideoRepository(Callback callback) {
//        this.callback = callback;
//        mUtils = new MUtils(new Handler(Looper.getMainLooper()));
//    }
//
//    public static ProgressDialog pd;
//
//    public SharedPreferences prefs;
//    public static boolean fromService;
//
//
//    public void makeDownload(final Context context, String url, Boolean service, boolean removeWatermark) {
//        Pattern pattern = Pattern.compile(PROXY_PATTERN);
//        Matcher matcher = pattern.matcher(url);
//        while (matcher.find()) {
//            List<String> data = new ArrayList<>();
//            data.add(matcher.group(0));//not use
//            data.add(matcher.group(1));
//            data.add(matcher.group(2));
//            data.add(matcher.group(3));
//
//            //System.out.println(matcher.group(1) + " " + matcher.group(2) +" "+ matcher.group(3));
//
//            //Format data
////            try {
////                JsonObject proto = wrapMtprotoData(data);
////                if (proto != null) {
////                    arrayList.add(proto);
////                }
////            } catch (NumberFormatException e) {
////                e.printStackTrace();
////            }
//            //        Map<String, Object> m=new ArrayMap<>();
////        m.put(key, new MtprotoProxy("h","p","s","",true));
//            String code = "";
//            boolean enabled = true;
//
//            try {
//                String host = data.get(1);
////                if (host.trim().contains("Unknown")) {
////                }
//                int port = Integer.parseInt(data.get(2));
//                MtprotoProxy server = new MtprotoProxy(
//                        host, "" + port,
//                        matcher.group(3), code, enabled);
//                bbbbb(server);
//
//            } catch (Exception e) {
//                DLog.handleException(e);
//            }
//        }
//    }
//
//    public void bbbbb(MtprotoProxy server) {
//        mUtils.getIpInfo(Collections.singletonList(server), new MUtils.Callback<List<MtprotoProxy>>() {
//            @Override
//            public void onError(String error) {
//                DLog.d("@@" + error);
//            }
//
//            @Override
//            public void onResponse(List<MtprotoProxy> serverList) {
//                for (MtprotoProxy data : serverList) {
//                    String url = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret);
//                    String key = HashUtils.md5(url);
//                    //data.code=
//
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.REF_KEY_MTPROTO).child(key);
//                    reference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            //if (key.equals(snapshot.getKey())) {
//                            DLog.d("Success!");
//                            //}
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            DLog.d(error.getMessage());
//                        }
//                    });
//                    reference.setValue(data);
//                }
//
//            }
//        });
//    }
//
//    public void detectType(Context context, ProxyInfo data) {
//        mUtils.getProxyType(data, new MUtils.Callback<ProxyInfo>() {
//            @Override
//            public void onError(String error) {
//                Toast.makeText(context, "" + error, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onResponse(ProxyInfo info) {
//                Toast.makeText(context, "->>" + info, Toast.LENGTH_LONG).show();
//                updateServerData(info);
//            }
//        });
//    }
//
//    public void bbbbbProxyInfo(ProxyInfo server) {
//        mUtils.getIpInfoProxyInfo(Collections.singletonList(server),
//                new MUtils.Callback<List<ProxyInfo>>() {
//
//            @Override
//            public void onError(String error) {
//                DLog.d("@@" + error);
//            }
//
//            @Override
//            public void onResponse(List<ProxyInfo> serverList) {
//                for (ProxyInfo data : serverList) {
//                    updateServerData(data);
//                }
//            }
//        });
//    }
//
//    private void updateServerData(ProxyInfo data) {
//        String key = HashUtils.md5(data.proxyUrl);
//        //data.code=
//
//        DatabaseReference reference = FirebaseDatabase.getInstance()
//                .getReference(Config.REF_KEY_GLYPE).child(key);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                //if (key.equals(snapshot.getKey())) {
//                DLog.d("@@@ Success!");
//                //}
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                DLog.d("@@@ " + error.getMessage());
//            }
//        });
//        reference.setValue(data);
//    }
//
//
//}
