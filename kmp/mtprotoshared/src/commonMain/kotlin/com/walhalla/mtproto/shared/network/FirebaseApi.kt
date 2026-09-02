package com.walhalla.mtproto.shared.network

import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class FirebaseApi(
    private val client: HttpClient,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    },
) {
    suspend fun fetchMtprotoProxies(): Result<List<MtprotoProxy>> = runCatching {
        val body = client.get("${AppConfig.FIREBASE_BASE_URL}/${AppConfig.REF_KEY_MTPROTO}.json").body<String>()
        decodeMap(body, MtprotoProxy.serializer())
            .sortedByDescending { it.update_at }
    }

    suspend fun fetchWebProxies(): Result<List<ProxyInfo>> = runCatching {
        val body = client.get("${AppConfig.FIREBASE_BASE_URL}/${AppConfig.REF_KEY_GLYPE}.json").body<String>()
        decodeMap(body, ProxyInfo.serializer())
            .filter { it.enabled }
            .sortedByDescending { it.rate }
    }

    suspend fun saveMtprotoProxy(proxy: MtprotoProxy): Result<Unit> {
        val key = proxy.firebaseKey()
        if (key.isEmpty()) return Result.failure(IllegalArgumentException("Invalid proxy data"))
        return putValue(AppConfig.REF_KEY_MTPROTO, key, json.encodeToString(MtprotoProxy.serializer(), proxy))
    }

    suspend fun saveWebProxy(proxy: ProxyInfo): Result<Unit> {
        val key = proxy.firebaseKey()
        if (key.isEmpty()) return Result.failure(IllegalArgumentException("Invalid proxy data"))
        return putValue(AppConfig.REF_KEY_GLYPE, key, json.encodeToString(ProxyInfo.serializer(), proxy))
    }

    private suspend fun putValue(path: String, key: String, payload: String): Result<Unit> = runCatching {
        val response = client.put("${AppConfig.FIREBASE_BASE_URL}/$path/$key.json") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        if (!response.status.isSuccess()) {
            error("Firebase update failed: ${response.status.value}")
        }
    }

    private fun <T> decodeMap(body: String, serializer: kotlinx.serialization.KSerializer<T>): List<T> {
        if (body == "null" || body.isBlank()) return emptyList()
        val element = json.parseToJsonElement(body)
        if (element !is kotlinx.serialization.json.JsonObject) return emptyList()
        return element.mapNotNull { (_, value) ->
            runCatching { json.decodeFromJsonElement(serializer, value) }.getOrNull()
        }
    }
}
