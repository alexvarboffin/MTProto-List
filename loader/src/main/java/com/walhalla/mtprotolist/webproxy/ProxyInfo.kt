package com.walhalla.mtprotolist.webproxy

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class ProxyInfo  // Конструктор без параметров (нужен для обработки ProGuard)
    : Serializable {
    @JvmField
    @SerializedName("proxyUrl")
    @Expose
    var proxyUrl: String? = null

    @JvmField
    @SerializedName("ip")
    @Expose
    var ip: String? = null

    @SerializedName("lastChecked")
    @Expose
    var lastChecked: String? = null

    @JvmField
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



    //Extended
    @JvmField
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

    @SerializedName("isActive")
    @Expose
    var isActive: String? = null


    @SerializedName("enabled")
    @Expose
    var enabled: Boolean = false
    //    @Override
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

