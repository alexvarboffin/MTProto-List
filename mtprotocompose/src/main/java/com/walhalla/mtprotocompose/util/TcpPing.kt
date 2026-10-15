package com.walhalla.mtprotocompose.util

import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

suspend fun tcpPingMs(
    host: String,
    port: Int,
    timeoutMs: Int = 2500,
): Long? = withContext(Dispatchers.IO) {
    if (host.isBlank() || port !in 1..65535) return@withContext null
    val started = SystemClock.elapsedRealtime()
    try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), timeoutMs)
        }
        SystemClock.elapsedRealtime() - started
    } catch (_: Exception) {
        null
    }
}
