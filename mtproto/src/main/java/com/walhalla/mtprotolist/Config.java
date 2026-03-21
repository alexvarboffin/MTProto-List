package com.walhalla.mtprotolist;


import com.walhalla.ui.BuildConfig;

public class Config {

    public static final boolean BuildConfigDEBUG = BuildConfig.DEBUG;

    public static final String KEY_CITY = "city";
    public static final String KEY_REGION_NAME = "regionName";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";


    public static final String KEY_CLIPBOARD_MONITOR = "cm_running_on";
    public static final String KEY_TKT_LOADER = "tkt__";

    public static final String handler = "file:///android_asset/flag/%1$s.png";
    public static final String URL_PRIVACY_POLICY = "https://mtprotolist.firebaseapp.com";
    public static final boolean ENABLE_NATIVE_ADS = true;
    public static final int BANNER_COUNTS = 4; //4

    //Шаблон баннера
//    public static final int ADS_BANNER_TYPE =
//            //RecyclerViewAdapter.UNIFIED_NATIVE_AD_VIEW_TYPE;
//            //RecyclerViewAdapter.NATIVE_MEDIUM;
//            BaseRecyclerViewAdapter.NATIVE_SMALL_TEMPLATE;
//            //RecyclerViewAdapter.NATIVE_TYPE_SIMPLE;

    public static final String PROXY_HANDLER = "https://t.me/proxy?server=%1$s&port=%2$s&secret=%3$s";
    public static final String PROXY_HANDLER_TG = "tg://proxy?server=%1$s&port=%2$s&secret=%3$s";


    public static final String REF_KEY_MTPROTO = "a";
    public static final String REF_KEY_GLYPE = "b";

}
