package com.walhalla.mtprotolist.util

import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import kotlinx.serialization.json.Json

object ProxyDialogArgs {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    fun encodeMtproto(proxy: MtprotoProxy?): String? {
        return proxy?.let { json.encodeToString(MtprotoProxy.serializer(), it) }
    }

    fun decodeMtproto(raw: String?): MtprotoProxy? {
        if (raw.isNullOrBlank()) return null
        return runCatching { json.decodeFromString(MtprotoProxy.serializer(), raw) }.getOrNull()
    }

    fun encodeWebProxy(proxy: ProxyInfo?): String? {
        return proxy?.let { json.encodeToString(ProxyInfo.serializer(), it) }
    }

    fun decodeWebProxy(raw: String?): ProxyInfo? {
        if (raw.isNullOrBlank()) return null
        return runCatching { json.decodeFromString(ProxyInfo.serializer(), raw) }.getOrNull()
    }
}
