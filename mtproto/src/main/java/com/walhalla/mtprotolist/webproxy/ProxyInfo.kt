package com.walhalla.mtprotolist.webproxy

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class ProxyInfo : Serializable {
    
    @JvmField
    @SerializedName("proxyUrl")
    @Expose
    var proxyUrl: String? = null

    @JvmField
    @SerializedName("ip")
    @Expose
    var ip: String? = null

    @JvmField
    @SerializedName("lastChecked")
    @Expose
    var lastChecked: String? = null

    @JvmField
    @SerializedName("type")
    @Expose
    var type: String? = null

    @JvmField
    @SerializedName("ssl")
    @Expose
    var ssl: String? = null

    @JvmField
    @SerializedName("speed")
    @Expose
    var speed: String? = null

    @JvmField
    @SerializedName("rate")
    @Expose
    var rate: Int = 0

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

    //Extended
    
    @JvmField
    @SerializedName("code")
    @Expose
    var code: String = ""

    @JvmField
    @SerializedName("city")
    @Expose
    var city: String? = null

    @JvmField
    @SerializedName("regionName")
    @Expose
    var regionName: String? = null

    @JvmField
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0

    @JvmField
    @SerializedName("lon")
    @Expose
    var lon: Double = 0.0

    @JvmField
    @SerializedName("country")
    @Expose
    var country: String? = null

    @JvmField
    @SerializedName("region")
    @Expose
    var region: String? = null

    @JvmField
    @SerializedName("zip")
    @Expose
    var zip: String? = null

    @JvmField
    @SerializedName("timezone")
    @Expose
    var timezone: String? = null

    @JvmField
    @SerializedName("isp")
    @Expose
    var isp: String? = null

    @JvmField
    @SerializedName("org")
    @Expose
    var org: String? = null

    @JvmField
    @SerializedName("as")
    @Expose
    var `as`: String? = null

    @JvmField
    @SerializedName("isActive")
    @Expose
    var isActive: String? = null

    @JvmField
    @SerializedName("enabled")
    @Expose
    var enabled: Boolean = false

    @JvmField
    @SerializedName("update_at")
    @Expose
    var update_at: Long = 0 //    @Override
    //    public String toString() {
    //        return "proxyURL='" + proxyUrl + '\'' +
    //                ", ipAddress='" + ip + '\'' +
    //                ", lastChecked='" + lastChecked + '\'' +
    //                ", type='" + type + '\'' +
    //                ", ssl='" + ssl + '\'' +
    //                ", speed='" + speed + '\'' +
    //                ", code='" + code + '\'' +
    //                ", city='" + city + '\'' +
    //                ", regionName='" + regionName + '\'' +
    //                ", lat=" + lat +
    //                ", lon=" + lon +
    //                ", country='" + country + '\'' +
    //                ", region='" + region + '\'' +
    //                ", zip='" + zip + '\'' +
    //                ", timezone='" + timezone + '\'' +
    //                ", isp='" + isp + '\'' +
    //                ", org='" + org + '\'' +
    //                ", as='" + as + '\'' +
    //                '}';
    //    }
}

