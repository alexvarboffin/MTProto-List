package com.walhalla.mtprotolist.manager

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
import org.json.JSONObject
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MUtils(private val h: Handler) {
    private val executor: Executor = Executors.newCachedThreadPool()

    interface Callback<T> {
        fun onError(error: String?)

        fun onResponse(serverList: T?)
    }

    private val client = OkHttpClient()
    private val urlCheckIpBatch = "http://ip-api.com/batch"

    fun getIpInfo(
        serverList: List<MtprotoProxy>,
        callback: Callback<List<MtprotoProxy>>,
    ) {
        executor.execute {
            try {
                val jsonArray = getArray0(serverList)
                if (jsonArray.length() != serverList.size) {
                    h.post { callback.onError("Host resolve failed") }
                    return@execute
                }
                val JSON = MediaType.parse("application/json; charset=utf-8")
                val requestBody = RequestBody.create(JSON, jsonArray.toString())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful()) {
                    val jsonResponse = JSONArray(response.body()!!.string())
                    h.post {
                        val geoError = applyGeoResponseToMtproto(jsonResponse, serverList)
                        if (geoError == null) {
                            callback.onResponse(serverList)
                        } else {
                            callback.onError(geoError)
                        }
                    }
                } else {
                    val errorMessage = response.message()
                    h.post { callback.onError(errorMessage) }
                }
            } catch (e: Exception) {
                handleException(e)
                val errorMessage = e.message
                h.post { callback.onError(errorMessage) }
            }
        }
    }

    fun getIpInfoProxyInfo(
        serverList: MutableList<ProxyInfo>,
        callback: Callback<List<ProxyInfo>>,
    ) {
        executor.execute {
            try {
                val jsonArray = getQueryArray(serverList)
                if (jsonArray.length() != serverList.size) {
                    h.post { callback.onError("Host resolve failed") }
                    return@execute
                }
                val JSON = MediaType.parse("application/json; charset=utf-8")
                val requestBody = RequestBody.create(JSON, jsonArray.toString())

                val request = Request.Builder()
                    .url(urlCheckIpBatch)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful()) {
                    val jsonResponse = JSONArray(response.body()!!.string())
                    h.post {
                        val geoError = applyGeoResponseToProxyInfo(jsonResponse, serverList)
                        if (geoError == null) {
                            callback.onResponse(serverList)
                        } else {
                            callback.onError(geoError)
                        }
                    }
                } else {
                    val errorMessage = response.message()
                    h.post { callback.onError(errorMessage) }
                }
            } catch (e: Exception) {
                handleException(e)
                val errorMessage = e.message
                h.post { callback.onError(errorMessage) }
            }
        }
    }

    private fun applyGeoResponseToProxyInfo(response: JSONArray, serverList: MutableList<ProxyInfo>): String? {
        if (response.length() != serverList.size) {
            return "Geo lookup failed"
        }

        for (i in 0..<response.length()) {
            val ipInfo = response.getJSONObject(i)
            d("@@@@@@@" + ipInfo)

            val geoError = validateGeoResponse(ipInfo)
            if (geoError != null) {
                return geoError
            }

            try {
                applyGeoToProxyInfo(serverList[i], ipInfo)
            } catch (e: JSONException) {
                handleException(e)
                return "Geo lookup failed"
            }
        }
        return null
    }

    private fun applyGeoResponseToMtproto(response: JSONArray, serverList: List<MtprotoProxy>): String? {
        if (response.length() != serverList.size) {
            return "Geo lookup failed"
        }

        for (i in 0..<response.length()) {
            val ipInfo = response.getJSONObject(i)
            d("@@@@@@@$ipInfo")

            val geoError = validateGeoResponse(ipInfo)
            if (geoError != null) {
                return geoError
            }

            try {
                applyGeoToMtproto(serverList[i], ipInfo)
            } catch (e: JSONException) {
                handleException(e)
                return "Geo lookup failed"
            }
        }
        return null
    }

    private fun validateGeoResponse(ipInfo: JSONObject): String? {
        when (ipInfo.optString("status")) {
            "fail" -> return ipInfo.optString("message", "Geo lookup failed")
            "success" -> Unit
            else -> if (ipInfo.optString("country").isBlank()) {
                return "Geo lookup failed"
            }
        }

        if (ipInfo.optString("country").isBlank() && ipInfo.optString("countryCode").isBlank()) {
            return "Empty geo data"
        }

        return null
    }

    private fun applyGeoToProxyInfo(server: ProxyInfo, ipInfo: JSONObject) {
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
    }

    private fun applyGeoToMtproto(server: MtprotoProxy, ipInfo: JSONObject) {
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
    }
}
