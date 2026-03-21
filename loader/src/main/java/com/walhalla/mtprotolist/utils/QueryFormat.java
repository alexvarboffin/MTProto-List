package com.walhalla.mtprotolist.utils;

import com.walhalla.mtprotolist.entity.MtprotoProxy;
import com.walhalla.mtprotolist.webproxy.ProxyInfo;
import com.walhalla.ui.DLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.IDN;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class QueryFormat {
    public static JSONArray getArray0(List<MtprotoProxy> serverList) {
        JSONArray jsonArray = new JSONArray();
        for (MtprotoProxy server : serverList) {
            JSONObject jsonObject = new JSONObject();
            try {
                try {
                    InetAddress address = InetAddress.getByName(server.host);
                    String ipAddress = address.getHostAddress();
                    jsonObject.put("query", ipAddress);
                    jsonObject.put("lang", "EN");//Locale.getDefault().getLanguage()
                    jsonArray.put(jsonObject);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                DLog.handleException(e);
            }
        }
        return jsonArray;
    }

    public static JSONArray getQueryArray(List<ProxyInfo> serverList) {
        JSONArray jsonArray = new JSONArray();
        for (ProxyInfo server : serverList) {
            JSONObject jsonObject = new JSONObject();
            try {
                try {
                    String urlString = server.proxyUrl;
                    URL url = new URL(urlString);
                    //String host = url.getHost();
                    String host = IDN.toASCII(url.getHost());

                    InetAddress address = InetAddress.getByName(host);
                    String ipAddress = address.getHostAddress();
                    jsonObject.put("query", ipAddress);
                    jsonObject.put("lang", "EN");//Locale.getDefault().getLanguage()
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    DLog.handleException(e);
                }
            } catch (Exception e) {
                DLog.handleException(e);
            }
        }
        return jsonArray;
    }
}
