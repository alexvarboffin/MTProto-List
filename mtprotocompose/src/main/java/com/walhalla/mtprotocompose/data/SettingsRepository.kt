package com.walhalla.mtprotocompose.data

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isNightMode(): Boolean = prefs.getBoolean(KEY_NIGHT_MODE, false)

    fun setNightMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NIGHT_MODE, enabled) }
    }

    companion object {
        private const val PREFS_NAME = "status_app"
        private const val KEY_NIGHT_MODE = "NightMode"
    }
}
