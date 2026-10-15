package com.walhalla.mtprotocompose.data

import java.util.Locale

fun localeFromLanguageCode(value: String): Locale = when (value) {
    "zh-rCN" -> Locale.SIMPLIFIED_CHINESE
    "zh-rTW" -> Locale.TRADITIONAL_CHINESE
    else -> if (value.contains("-")) {
        val language = value.substringBefore("-")
        val region = value.substringAfter("-").removePrefix("r")
        Locale(language, region)
    } else {
        Locale(value)
    }
}

fun localeMatchesLanguageCode(locale: Locale, value: String): Boolean {
    if (value.contains("-")) {
        val language = value.substringBefore("-")
        val region = value.substringAfter("-").removePrefix("r")
        return locale.language.equals(language, ignoreCase = true) &&
            locale.country.equals(region, ignoreCase = true)
    }
    return locale.language.equals(value, ignoreCase = true)
}
