package com.walhalla.mtprotolist.manager

import android.os.Handler
import android.os.Looper
import com.google.firebase.database.FirebaseDatabase
import com.walhalla.mtprotolist.Config
import com.walhalla.mtprotolist.HashUtils
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo

class VideoRepository {
    private val mUtils = MUtils(Handler(Looper.getMainLooper()))

    fun updateMtprotoGeo(
        server: MtprotoProxy,
        onSuccess: (MtprotoProxy) -> Unit,
        onError: (String?) -> Unit,
    ) {
        mUtils.getIpInfo(
            listOf(server),
            object : MUtils.Callback<List<MtprotoProxy>> {
                override fun onError(error: String?) {
                    onError(error)
                }

                override fun onResponse(serverList: List<MtprotoProxy>?) {
                    val data = serverList?.firstOrNull()
                    if (data == null) {
                        onError("Empty response")
                        return
                    }
                    if (!hasValidGeo(data)) {
                        onError("Empty geo data")
                        return
                    }
                    pushMtprotoToFirebase(data, onSuccess, onError)
                }
            },
        )
    }

    fun updateProxyInfoGeo(
        server: ProxyInfo,
        onSuccess: (ProxyInfo) -> Unit,
        onError: (String?) -> Unit,
    ) {
        mUtils.getIpInfoProxyInfo(
            mutableListOf(server),
            object : MUtils.Callback<List<ProxyInfo>> {
                override fun onError(error: String?) {
                    onError(error)
                }

                override fun onResponse(serverList: List<ProxyInfo>?) {
                    val data = serverList?.firstOrNull()
                    if (data == null) {
                        onError("Empty response")
                        return
                    }
                    if (!hasValidGeo(data)) {
                        onError("Empty geo data")
                        return
                    }
                    pushProxyInfoToFirebase(data, onSuccess, onError)
                }
            },
        )
    }

    private fun pushMtprotoToFirebase(
        data: MtprotoProxy,
        onSuccess: (MtprotoProxy) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val url = String.format(Config.PROXY_HANDLER, data.host, data.port, data.secret)
        val key = HashUtils.md5(url)
        FirebaseDatabase.getInstance()
            .getReference(Config.REF_KEY_MTPROTO)
            .child(key)
            .setValue(data)
            .addOnSuccessListener { onSuccess(data) }
            .addOnFailureListener { onError(it.message) }
    }

    private fun pushProxyInfoToFirebase(
        data: ProxyInfo,
        onSuccess: (ProxyInfo) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val proxyUrl = data.proxyUrl?.trim().orEmpty()
        if (proxyUrl.isEmpty()) {
            onError("Invalid proxy data")
            return
        }
        val key = HashUtils.md5(proxyUrl)
        FirebaseDatabase.getInstance()
            .getReference(Config.REF_KEY_GLYPE)
            .child(key)
            .setValue(data)
            .addOnSuccessListener { onSuccess(data) }
            .addOnFailureListener { onError(it.message) }
    }

    private fun hasValidGeo(data: MtprotoProxy): Boolean {
        return !data.country.isNullOrBlank() || !data.code.isNullOrBlank()
    }

    private fun hasValidGeo(data: ProxyInfo): Boolean {
        return !data.country.isNullOrBlank() || data.code.isNotBlank()
    }
}
