package com.walhalla.mtprotocompose.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    private val _nightMode = MutableStateFlow(prefs.getBoolean(KEY_NIGHT_MODE, false))
    val nightMode: StateFlow<Boolean> = _nightMode.asStateFlow()

    private val _languageCode = MutableStateFlow(
        defaultPrefs.getString(LocalePersistor.KEY_LANGUAGE, null),
    )
    val languageCode: StateFlow<String?> = _languageCode.asStateFlow()

    fun isNightMode(): Boolean = _nightMode.value

    fun setNightMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NIGHT_MODE, enabled) }
        _nightMode.value = enabled
    }

    fun setLanguageCode(code: String) {
        defaultPrefs.edit { putString(LocalePersistor.KEY_LANGUAGE, code) }
        _languageCode.value = code
    }

    companion object {
        private const val PREFS_NAME = "status_app"
        private const val KEY_NIGHT_MODE = "NightMode"
    }
}
