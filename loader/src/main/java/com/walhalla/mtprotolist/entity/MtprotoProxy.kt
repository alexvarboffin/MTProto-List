package com.walhalla.mtprotolist.entity

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class MtprotoProxy : Serializable {
    @JvmField
    var _id: Long? = null

    @JvmField
    @SerializedName("host")
    @Expose
    var host: String? = ""

    @JvmField
    @SerializedName("port")
    @Expose
    var port: String? = ""

    @JvmField
    @SerializedName("secret")
    @Expose
    var secret: String? = ""

    @JvmField
    @SerializedName("code")
    @Expose
    var code: String? = ""

    @JvmField
    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null


    @JvmField
    @SerializedName("update_at")
    @Expose
    var update_at: Long = 0


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
    @SerializedName("lock")
    @Expose
    var lock: Int = 0

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


    constructor(host: String?, port: String?, secret: String?, code: String?, enabled: Boolean?) {
        this.host = host
        this.port = port
        this.secret = secret
        this.code = code
        this.enabled = enabled
    }

    constructor() //FB
}
