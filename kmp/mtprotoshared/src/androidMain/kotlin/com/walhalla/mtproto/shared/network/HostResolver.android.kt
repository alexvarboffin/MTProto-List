package com.walhalla.mtproto.shared.network

import java.net.IDN
import java.net.InetAddress
import java.net.URL

actual fun resolveHostToIp(host: String): String? {
    val normalized = host.trim()
    if (normalized.isEmpty()) return null
    return try {
        InetAddress.getByName(normalized).hostAddress
    } catch (_: Exception) {
        null
    }
}

actual fun resolveWebProxyHost(proxyUrl: String): String? {
    return try {
        val url = URL(proxyUrl.trim())
        val host = url.host ?: return null
        resolveHostToIp(IDN.toASCII(host))
    } catch (_: Exception) {
        null
    }
}
