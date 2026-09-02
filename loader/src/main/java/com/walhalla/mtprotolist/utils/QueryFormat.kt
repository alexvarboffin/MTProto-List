package com.walhalla.mtprotolist.utils

import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.ui.DLog.handleException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.IDN
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException

object QueryFormat {
    fun getArray0(serverList: List<MtprotoProxy>): JSONArray {
        val jsonArray = JSONArray()
        for (server in serverList) {
            val jsonObject = JSONObject()
            try {
                try {
                    val address = InetAddress.getByName(server.host)
                    val ipAddress = address.hostAddress
                    jsonObject.put("query", ipAddress)
                    jsonObject.put("lang", "EN") //Locale.getDefault().getLanguage()
                    jsonArray.put(jsonObject)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }
            } catch (e: JSONException) {
                handleException(e)
            }
        }
        return jsonArray
    }

    fun getQueryArray(serverList: List<ProxyInfo>): JSONArray {
        val jsonArray = JSONArray()
        for (server in serverList) {
            val jsonObject = JSONObject()
            try {
                try {
                    val urlString = server.proxyUrl
                    val url = URL(urlString)
                    //String host = url.getHost();
                    val host = IDN.toASCII(url.host)

                    val address = InetAddress.getByName(host)
                    val ipAddress = address.hostAddress
                    jsonObject.put("query", ipAddress)
                    jsonObject.put("lang", "EN") //Locale.getDefault().getLanguage()
                    jsonArray.put(jsonObject)
                } catch (e: Exception) {
                    handleException(e)
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
        return jsonArray
    }
}
