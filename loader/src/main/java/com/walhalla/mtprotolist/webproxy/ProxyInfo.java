package com.walhalla.mtprotolist.webproxy;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ProxyInfo implements Serializable {

    @SerializedName("proxyUrl")
    @Expose
    public String proxyUrl;

    @SerializedName("ip")
    @Expose
    public String ip;

    @SerializedName("lastChecked")
    @Expose
    public String lastChecked;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("ssl")
    @Expose
    public String ssl;

    @SerializedName("speed")
    @Expose
    public String speed;


    @SerializedName("rate")
    @Expose
    public int rate;

    // Конструктор без параметров (нужен для обработки ProGuard)
    public ProxyInfo() {
    }


    public void setCity(String city) {
        this.city = city;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setAs(String as) {
        this.as = as;
    }

    //Extended

    @SerializedName("code")
    @Expose
    public String code = "";

    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("regionName")
    @Expose
    public String regionName;
    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("lon")
    @Expose
    public double lon;

    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("region")
    @Expose
    public String region;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("timezone")
    @Expose
    public String timezone;
    @SerializedName("isp")
    @Expose
    public String isp;

    @SerializedName("org")
    @Expose
    public String org;

    @SerializedName("as")
    @Expose
    public String as;

    @SerializedName("isActive")
    @Expose
    public String isActive;


    @SerializedName("enabled")
    @Expose
    public boolean enabled;

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

