package com.walhalla.mtprotolist.webproxy

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class ProxyInfo : Serializable {
    
    @SerializedName("proxyUrl")
    @Expose
    var proxyUrl: String? = null

    
    @SerializedName("ip")
    @Expose
    var ip: String? = null

    @SerializedName("lastChecked")
    @Expose
    var lastChecked: String? = null

    
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("ssl")
    @Expose
    var ssl: String? = null

    @SerializedName("speed")
    @Expose
    var speed: String? = null


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
    
    @SerializedName("code")
    @Expose
    var code: String = ""

    
    @SerializedName("city")
    @Expose
    var city: String? = null

    
    @SerializedName("regionName")
    @Expose
    var regionName: String? = null

    
    @SerializedName("lat")
    @Expose
    var lat: Double = 0.0

    
    @SerializedName("lon")
    @Expose
    var lon: Double = 0.0

    
    @SerializedName("country")
    @Expose
    var country: String? = null

    
    @SerializedName("region")
    @Expose
    var region: String? = null

    
    @SerializedName("zip")
    @Expose
    var zip: String? = null

    
    @SerializedName("timezone")
    @Expose
    var timezone: String? = null

    
    @SerializedName("isp")
    @Expose
    var isp: String? = null

    
    @SerializedName("org")
    @Expose
    var org: String? = null

    
    @SerializedName("as")
    @Expose
    var `as`: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: String? = null


    
    @SerializedName("enabled")
    @Expose
    var enabled: Boolean = false


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

