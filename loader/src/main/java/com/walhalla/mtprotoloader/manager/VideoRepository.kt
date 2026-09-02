package com.walhalla.mtprotoloader.manager

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.utils.HashUtils
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import java.util.regex.Pattern

//package com.walhalla.mtprotolist.manager;

class VideoRepository(private val callback: Callback) {
    private val mUtils: MUtils


    //private static GetVideo getVideo;
    //public static final int ERROR_WENT_WRONG = R.string.abc_something_went_wrong;
    interface Callback {
        fun successResult(result: String?)
    }

    var prefs: SharedPreferences? = null

    init {
        mUtils = MUtils(Handler(Looper.getMainLooper()))
    }

    fun makeDownload(context: Context?, url: String, service: Boolean?, removeWatermark: Boolean) {
        val pattern = Pattern.compile(PROXY_PATTERN)
        val matcher = pattern.matcher(url)
        while (matcher.find()) {
            val data: MutableList<String?> = ArrayList<String?>()
            data.add(matcher.group(0)) //not use
            data.add(matcher.group(1))
            data.add(matcher.group(2))
            data.add(matcher.group(3))

            //System.out.println(matcher.group(1) + " " + matcher.group(2) +" "+ matcher.group(3));

            //Format data
//            try {
//                JsonObject proto = wrapMtprotoData(data);
//                if (proto != null) {
//                    arrayList.add(proto);
//                }
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
            //        Map<String, Object> m=new ArrayMap<>();
//        m.put(key, new MtprotoProxy("h","p","s","",true));
            val code = ""
            val enabled = true

            try {
                val host = data.get(1)
                //                if (host.trim().contains("Unknown")) {
//                }
                val port = data.get(2)!!.toInt()
                val server = MtprotoProxy(host, "" + port, matcher.group(3), code, enabled)
                server.update_at = System.currentTimeMillis()
                bbbbb(server)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun bbbbb(server: MtprotoProxy?) {
        mUtils.getIpInfo(
            mutableListOf(server!!),
            object : MUtils.Callback<List<MtprotoProxy>> {
                override fun onError(error: String?) {
                    d("@@$error")
                }

                override fun onResponse(serverList: List<MtprotoProxy>?) {
                    if (serverList == null) return
                    for (data in serverList) {
                        val url =
                            String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret)
                        val key = HashUtils.md5(url)

                        //data.code=
                        val reference = FirebaseDatabase.getInstance()
                            .getReference(Config.REF_KEY_MTPROTO)
                            .child(key)
                        reference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //if (key.equals(snapshot.getKey())) {
                                d("Success!")
                                //}
                            }

                            override fun onCancelled(error: DatabaseError) {
                                d(error.message)
                            }
                        })
                        reference.setValue(data)
                    }
                }
            })
    }

    fun detectType(context: Context?, data: ProxyInfo) {
        mUtils.getProxyType(data, object : MUtils.Callback<ProxyInfo> {
            override fun onError(error: String?) {
                Toast.makeText(context, "" + error, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(info: ProxyInfo?) {
                Toast.makeText(context, "->>$info", Toast.LENGTH_LONG).show()
                if(info!=null){
                    updateServerData(info)
                }
            }
        })
    }

    fun bbbbbProxyInfo(server: ProxyInfo?) {
        mUtils.getIpInfoProxyInfo(
            mutableListOf(server!!),
            object : MUtils.Callback<List<ProxyInfo>> {
                override fun onError(error: String?) {
                    d("@@$error")
                }

                override fun onResponse(serverList: List<ProxyInfo>?) {
                    if (serverList == null) return
                    for (data in serverList) {
                        updateServerData(data)
                    }
                }
            })
    }

    private fun updateServerData(data: ProxyInfo) {
        val key = HashUtils.md5(data.proxyUrl)

        //data.code=
        val reference = FirebaseDatabase.getInstance()
            .getReference(Config.REF_KEY_GLYPE).child(key)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //if (key.equals(snapshot.getKey())) {
                d("@@@ Success!")
                //}
            }

            override fun onCancelled(error: DatabaseError) {
                d("@@@ " + error.message)
            }
        })
        reference.setValue(data)
    }


    companion object {
        private const val PROXY_PATTERN = "https://t.me/proxy\\?server=(.*?)&port=(.*?)&secret=(.*)"


        var pd: ProgressDialog? = null

        var fromService: Boolean = false
    }
}
