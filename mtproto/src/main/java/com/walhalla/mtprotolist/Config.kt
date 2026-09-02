package com.walhalla.mtprotolist


object Config {
    val BuildConfigDEBUG: Boolean = BuildConfig.DEBUG

    const val KEY_CITY: String = "city"
    const val KEY_REGION_NAME: String = "regionName"
    const val KEY_LAT: String = "lat"
    const val KEY_LON: String = "lon"


    const val KEY_CLIPBOARD_MONITOR: String = "cm_running_on"
    const val KEY_TKT_LOADER: String = "tkt__"

    const val ASSET_HANDLER: String = "file:///android_asset/flag/%1\$s.png"
    const val URL_PRIVACY_POLICY: String = "https://mtprotolist.firebaseapp.com"
    const val ENABLE_NATIVE_ADS: Boolean = true
    const val BANNER_COUNTS: Int = 4 //4

    //Шаблон баннера
    //    public static final int ADS_BANNER_TYPE =
    //            //RecyclerViewAdapter.UNIFIED_NATIVE_AD_VIEW_TYPE;
    //            //RecyclerViewAdapter.NATIVE_MEDIUM;
    //            BaseRecyclerViewAdapter.NATIVE_SMALL_TEMPLATE;
    //            //RecyclerViewAdapter.NATIVE_TYPE_SIMPLE;
    const val PROXY_HANDLER: String = "https://t.me/proxy?server=%1\$s&port=%2\$s&secret=%3\$s"
    const val PROXY_HANDLER_TG: String = "tg://proxy?server=%1\$s&port=%2\$s&secret=%3\$s"


    const val REF_KEY_MTPROTO: String = "a"
    const val REF_KEY_GLYPE: String = "b"
}
