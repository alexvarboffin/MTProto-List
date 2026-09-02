package com.walhalla.mtprotolist.webproxy

import com.walhalla.mtproto.shared.util.HashUtils
import kotlinx.serialization.Serializable

@Serializable
class ProxyInfo {
    @JvmField
    var proxyUrl: String? = null

    @JvmField
    var ip: String? = null

    @JvmField
    var lastChecked: String? = null

    @JvmField
    var type: String? = null

    @JvmField
    var ssl: String? = null

    @JvmField
    var speed: String? = null

    @JvmField
    var rate: Int = 0

    @JvmField
    var code: String = ""

    @JvmField
    var city: String? = null

    @JvmField
    var regionName: String? = null

    @JvmField
    var lat: Double = 0.0

    @JvmField
    var lon: Double = 0.0

    @JvmField
    var country: String? = null

    @JvmField
    var region: String? = null

    @JvmField
    var zip: String? = null

    @JvmField
    var timezone: String? = null

    @JvmField
    var isp: String? = null

    @JvmField
    var org: String? = null

    @JvmField
    var `as`: String? = null

    @JvmField
    var isActive: String? = null

    @JvmField
    var enabled: Boolean = false

    @JvmField
    var update_at: Long = 0L

    fun setCity(city: String?) {
        this.city = city
    }

    fun setRegionName(regionName: String?) {
        this.regionName = regionName
    }

    fun setLat(lat: Double) {
        this.lat = lat
    }

    fun setLon(lon: Double) {
        this.lon = lon
    }

    fun setCountry(country: String?) {
        this.country = country
    }

    fun setRegion(region: String?) {
        this.region = region
    }

    fun setZip(zip: String?) {
        this.zip = zip
    }

    fun setTimezone(timezone: String?) {
        this.timezone = timezone
    }

    fun setIsp(isp: String?) {
        this.isp = isp
    }

    fun setOrg(org: String?) {
        this.org = org
    }

    fun setAs(`as`: String?) {
        this.`as` = `as`
    }

    fun firebaseKey(): String = HashUtils.md5(proxyUrl?.trim().orEmpty())
}
