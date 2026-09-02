package com.walhalla.mtprotoloader.manager

import android.os.Handler
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.utils.QueryFormat.getArray0
import com.walhalla.mtprotolist.utils.QueryFormat.getQueryArray
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//package com.walhalla.mtprotolist.manager;

class MUtils(private val h: Handler) {
    private val executor: Executor = Executors.newCachedThreadPool() // Пул потоков

    fun getProxyType(info: ProxyInfo, callback: Callback<ProxyInfo>) {
        if(info.proxyUrl == null) return

        executor.execute {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(info.proxyUrl?:"")
                .build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (responseBody.contains("Glype")) {
                    h.post {
                        info.type = "Glype"
                        callback.onResponse(info)
                    }
                } else if (responseBody.contains("PHP-Proxy")) {
                    h.post {
                        info.type = "PHP-Proxy"
                        callback.onResponse(info)
                    }
                } else if (responseBody.contains("PHProxy")) {
                    h.post {
                        info.type = "PHProxy"
                        callback.onResponse(info)
                    }
                } else {
                    h.post {
                        info.type = "Other"
                        callback.onResponse(info)
                    }
                }
            } catch (e: Exception) {
                h.post {
                    callback.onError(e.javaClass.simpleName)
                }
            }
        }
    }


    interface Callback<T> {
        fun onError(error: String?)

        fun onResponse(serverList: T?)
    }

    private val client = OkHttpClient()
    private val urlCheckIpBatch = "http://ip-api.com/batch"

    fun getIpInfo(
        serverList: List<MtprotoProxy>,
        callback: Callback<List<MtprotoProxy>>
    ) {
        executor.execute(Runnable {
            try {
                val jsonArray = getArray0(serverList)
                val requestBody = jsonArray.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = JSONArray(response.body?.string().orEmpty())
                    h.post {
                        if (setIpInfo(jsonResponse, serverList)) {
                            callback.onResponse(serverList)
                        }
                    }
                } else {
                    val errorMessage = response.message
                    h.post(Runnable { callback.onError(errorMessage) })
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = e.message // Обработка исключений
                h.post(object : Runnable {
                    override fun run() {
                        callback.onError(errorMessage)
                    }
                })
            }
        })
    }

    fun getIpInfoProxyInfo(
        serverList: MutableList<ProxyInfo>,
        callback: Callback<List<ProxyInfo>>
    ) {
        executor.execute {
            try {
                val jsonArray = getQueryArray(serverList)
                val requestBody = jsonArray.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = JSONArray(response.body?.string().orEmpty())
                    h.post(Runnable {
                        if (setIpInfo1(jsonResponse, serverList)) {
                            callback.onResponse(serverList)
                        }
                    })
                } else {
                    val errorMessage = response.message
                    h.post { callback.onError(errorMessage) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = e.message // Обработка исключений
                h.post { callback.onError(errorMessage) }
            }
        }
    }

    private fun setIpInfo1(response: JSONArray, serverList: MutableList<ProxyInfo>): Boolean {
        var result = false

        for (i in 0..<response.length()) {
            try {
                // ip ==> ipInfo.get("query").toString()
                val ipInfo = response.getJSONObject(i)
                d("@@@@@@@" + ipInfo)

                val server = serverList.get(i)
                server.city = ipInfo.getString(Config.KEY_CITY)
                server.regionName = ipInfo.getString(Config.KEY_REGION_NAME)
                server.lat = ipInfo.getDouble(Config.KEY_LAT)
                server.lon = ipInfo.getDouble(Config.KEY_LON)
                server.code = ipInfo.getString("countryCode")

                if (server.ip!!.length < 6) {
                    server.ip = ipInfo.getString("query")
                }


                server.country = ipInfo.getString("country")
                server.region = ipInfo.getString("region")
                server.zip = ipInfo.getString("zip")

                server.timezone = ipInfo.getString("timezone")
                server.isp = ipInfo.getString("isp")
                server.org = ipInfo.getString("org")
                server.`as` = ipInfo.getString("as")
                result = true
            } catch (e: JSONException) {
                result = false
                handleException(e)
            }
        }
        return result
    }


    fun setIpInfo(response: JSONArray, serverList: List<MtprotoProxy>): Boolean {
        var result = false

        for (i in 0..<response.length()) {
            try {
                // ip ==> ipInfo.get("query").toString()
                val ipInfo = response.getJSONObject(i)
                d("@@@@@@@$ipInfo")

                val server = serverList[i]
                server.city = ipInfo.getString(Config.KEY_CITY)
                server.regionName = ipInfo.getString(Config.KEY_REGION_NAME)
                server.lat = ipInfo.getDouble(Config.KEY_LAT)
                server.lon = ipInfo.getDouble(Config.KEY_LON)
                server.code = ipInfo.getString("countryCode")


                server.country = ipInfo.getString("country")
                server.region = ipInfo.getString("region")
                server.zip = ipInfo.getString("zip")

                server.timezone = ipInfo.getString("timezone")
                server.isp = ipInfo.getString("isp")
                server.org = ipInfo.getString("org")
                server.`as` = ipInfo.getString("as")
                result = true
            } catch (e: JSONException) {
                result = false
                handleException(e)
            }
        }
        return result
    }
}
