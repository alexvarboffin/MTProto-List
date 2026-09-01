package com.walhalla.mtprotoloader.manager

import android.os.Handler
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.utils.QueryFormat.getArray0
import com.walhalla.mtprotolist.utils.QueryFormat.getQueryArray
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//package com.walhalla.mtprotolist.manager;

class MUtils(private val h: Handler) {
    private val executor: Executor = Executors.newCachedThreadPool() // Пул потоков

    fun getProxyType(info: ProxyInfo, callback: Callback<ProxyInfo>) {
        executor.execute {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(info.proxyUrl)
                .build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()!!.string()

                if (responseBody.contains("Glype")) {
                    h.post(Runnable {
                        info.type = "Glype"
                        callback.onResponse(info)
                    })
                } else if (responseBody.contains("PHP-Proxy")) {
                    h.post(Runnable {
                        info.type = "PHP-Proxy"
                        callback.onResponse(info)
                    })
                } else if (responseBody.contains("PHProxy")) {
                    h.post(Runnable {
                        info.type = "PHProxy"
                        callback.onResponse(info)
                    })
                } else {
                    h.post(Runnable {
                        info.type = "Other"
                        callback.onResponse(info)
                    })
                }
            } catch (e: Exception) {
                h.post(Runnable {
                    callback.onError(e.javaClass.getSimpleName())
                })
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
                val JSON = MediaType.parse("application/json; charset=utf-8")
                val requestBody = RequestBody.create(JSON, jsonArray.toString())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful()) {
                    // Обработка успешного ответа
                    val jsonResponse = JSONArray(response.body()!!.string())
                    h.post {
                        if (setIpInfo(jsonResponse, serverList)) {
                            callback.onResponse(serverList)
                        }
                    }
                } else {
                    // Обработка ошибки
                    val errorMessage = response.message()
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
                val JSON = MediaType.parse("application/json; charset=utf-8")
                val requestBody = RequestBody.create(JSON, jsonArray.toString())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful()) {
                    // Обработка успешного ответа
                    val jsonResponse = JSONArray(response.body()!!.string())
                    h.post(Runnable {
                        if (setIpInfo1(jsonResponse, serverList)) {
                            callback.onResponse(serverList)
                        }
                    })
                } else {
                    // Обработка ошибки
                    val errorMessage = response.message()
                    h.post { callback.onError(errorMessage) }
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
                server.setCity(ipInfo.getString(Config.KEY_CITY))
                server.setRegionName(ipInfo.getString(Config.KEY_REGION_NAME))
                server.setLat(ipInfo.getDouble(Config.KEY_LAT))
                server.setLon(ipInfo.getDouble(Config.KEY_LON))
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
