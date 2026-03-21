package com.walhalla.mtprotolist.entity;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walhalla.mtprotolist.LessonState;

import java.io.Serializable;

@Keep
public class MtprotoProxy implements Serializable {


    public Long _id;

    @SerializedName("host")
    @Expose
    public String host = "";
    @SerializedName("port")
    @Expose
    public String port = "";
    @SerializedName("secret")
    @Expose
    public String secret = "";
    @SerializedName("code")
    @Expose
    public String code = "";
    @SerializedName("enabled")
    @Expose
    public Boolean enabled;

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


//    @SerializedName("lock")
//    @Expose
//    private int lock;


    public MtprotoProxy(String host, String port, String secret, String code, Boolean enabled) {
        this.host = host;
        this.port = port;
        this.secret = secret;
        this.code = code;
        this.enabled = enabled;
    }

    public MtprotoProxy() {
    } //FB


//    public int getLock() {
//        return lock;
//    }
//
//    public void setLock(int lock) {
//        this.lock = lock;
//    }

}
