package com.walhalla.mtprotolist.entity

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class MtprotoProxy : Serializable {
    var _id: Long? = null

    @SerializedName("host")
    @Expose
    var host: String? = ""

    @SerializedName("port")
    @Expose
    var port: String? = ""

    @SerializedName("secret")
    @Expose
    var secret: String? = ""

    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null


    @SerializedName("update_at")
    @Expose
    var update_at: Long = 0


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


    @SerializedName("lock")
    @Expose
    var lock: Int = 0


    constructor(host: String?, port: String?, secret: String?, code: String?, enabled: Boolean?) {
        this.host = host
        this.port = port
        this.secret = secret
        this.code = code
        this.enabled = enabled
    }

    constructor() //FB
}
