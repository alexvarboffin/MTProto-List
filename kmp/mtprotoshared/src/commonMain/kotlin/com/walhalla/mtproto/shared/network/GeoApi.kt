package com.walhalla.mtproto.shared.network

import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class GeoApi(
    private val client: HttpClient,
) {
    suspend fun updateMtprotoGeo(proxy: MtprotoProxy): Result<MtprotoProxy> {
        val host = proxy.host?.trim().orEmpty()
        if (host.isEmpty()) return Result.failure(IllegalArgumentException("Invalid host"))
        val ip = resolveHostToIp(host)
            ?: return Result.failure(IllegalStateException("Host resolve failed"))
        return lookupSingle(ip).map { geo ->
            applyGeo(proxy, geo)
            proxy
        }
    }

    suspend fun updateWebProxyGeo(proxy: ProxyInfo): Result<ProxyInfo> {
        val ip = resolveWebProxyHost(proxy.proxyUrl.orEmpty())
            ?: return Result.failure(IllegalStateException("Host resolve failed"))
        return lookupSingle(ip).map { geo ->
            if (proxy.ip.orEmpty().length < 6) {
                proxy.ip = geo.query
            }
            applyGeo(proxy, geo)
            proxy
        }
    }

    private fun applyGeo(proxy: MtprotoProxy, geo: GeoRecord) {
        proxy.city = geo.city
        proxy.regionName = geo.regionName
        proxy.lat = geo.lat
        proxy.lon = geo.lon
        proxy.code = geo.countryCode
        proxy.country = geo.country
        proxy.region = geo.region
        proxy.zip = geo.zip
        proxy.timezone = geo.timezone
        proxy.isp = geo.isp
        proxy.org = geo.org
        proxy.`as` = geo.asn
    }

    private fun applyGeo(proxy: ProxyInfo, geo: GeoRecord) {
        proxy.city = geo.city
        proxy.regionName = geo.regionName
        proxy.lat = geo.lat
        proxy.lon = geo.lon
        proxy.code = geo.countryCode
        proxy.country = geo.country
        proxy.region = geo.region
        proxy.zip = geo.zip
        proxy.timezone = geo.timezone
        proxy.isp = geo.isp
        proxy.org = geo.org
        proxy.`as` = geo.asn
    }

    private suspend fun lookupSingle(ip: String): Result<GeoRecord> {
        val payload = buildJsonArray {
            add(
                buildJsonObject {
                    put("query", ip)
                    put("lang", "EN")
                },
            )
        }
        return runCatching {
            val response = client.post(AppConfig.IP_API_BATCH_URL) {
                contentType(ContentType.Application.Json)
                setBody(payload.toString())
            }.body<String>()
            val array = Json.parseToJsonElement(response) as JsonArray
            if (array.isEmpty()) error("Geo lookup failed")
            parseGeoRecord(array.first())
        }
    }

    private fun parseGeoRecord(element: JsonElement): GeoRecord {
        val obj = element as? JsonObject ?: error("Geo lookup failed")
        when (obj["status"]?.jsonPrimitive?.content) {
            "fail" -> error(obj["message"]?.jsonPrimitive?.content ?: "Geo lookup failed")
            "success" -> Unit
            else -> if (obj["country"]?.jsonPrimitive?.content.isNullOrBlank()) {
                error("Geo lookup failed")
            }
        }
        val country = obj["country"]?.jsonPrimitive?.content.orEmpty()
        val countryCode = obj["countryCode"]?.jsonPrimitive?.content.orEmpty()
        if (country.isBlank() && countryCode.isBlank()) {
            error("Empty geo data")
        }
        return GeoRecord(
            query = obj["query"]?.jsonPrimitive?.content.orEmpty(),
            city = obj["city"]?.jsonPrimitive?.content,
            regionName = obj["regionName"]?.jsonPrimitive?.content,
            lat = obj["lat"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            lon = obj["lon"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            countryCode = countryCode,
            country = country.ifBlank { null },
            region = obj["region"]?.jsonPrimitive?.content,
            zip = obj["zip"]?.jsonPrimitive?.content,
            timezone = obj["timezone"]?.jsonPrimitive?.content,
            isp = obj["isp"]?.jsonPrimitive?.content,
            org = obj["org"]?.jsonPrimitive?.content,
            asn = obj["as"]?.jsonPrimitive?.content,
        )
    }
}

@Serializable
private data class GeoRecord(
    val query: String = "",
    val city: String? = null,
    val regionName: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    @SerialName("countryCode")
    val countryCode: String = "",
    val country: String? = null,
    val region: String? = null,
    val zip: String? = null,
    val timezone: String? = null,
    val isp: String? = null,
    val org: String? = null,
    @SerialName("as")
    val asn: String? = null,
)
