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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
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
        proxy.ip = geo.query.ifBlank { proxy.ip }
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
        proxy.asname = geo.asname
        proxy.continent = geo.continent
        proxy.continentCode = geo.continentCode
        proxy.district = geo.district
        proxy.offset = geo.offset
        proxy.currency = geo.currency
        proxy.mobile = geo.mobile
        proxy.isProxy = geo.proxy
        proxy.hosting = geo.hosting
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
            query = obj.optString("query").orEmpty(),
            city = obj.optString("city"),
            regionName = obj.optString("regionName"),
            lat = obj.optString("lat")?.toDoubleOrNull() ?: 0.0,
            lon = obj.optString("lon")?.toDoubleOrNull() ?: 0.0,
            countryCode = countryCode,
            country = country.ifBlank { null },
            region = obj.optString("region"),
            zip = obj.optString("zip"),
            timezone = obj.optString("timezone"),
            isp = obj.optString("isp"),
            org = obj.optString("org"),
            asn = obj.optString("as"),
            asname = obj.optString("asname"),
            continent = obj.optString("continent"),
            continentCode = obj.optString("continentCode"),
            district = obj.optString("district"),
            offset = obj.optString("offset")?.toIntOrNull(),
            currency = obj.optString("currency"),
            mobile = obj.optBool("mobile"),
            proxy = obj.optBool("proxy"),
            hosting = obj.optBool("hosting"),
        )
    }
}

private fun JsonObject.optString(key: String): String? =
    (this[key] as? JsonPrimitive)?.contentOrNull

private fun JsonObject.optBool(key: String): Boolean? {
    val primitive = this[key] as? JsonPrimitive ?: return null
    return primitive.booleanOrNull ?: primitive.contentOrNull?.toBooleanStrictOrNull()
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
    val asname: String? = null,
    val continent: String? = null,
    val continentCode: String? = null,
    val district: String? = null,
    val offset: Int? = null,
    val currency: String? = null,
    val mobile: Boolean? = null,
    val proxy: Boolean? = null,
    val hosting: Boolean? = null,
)
