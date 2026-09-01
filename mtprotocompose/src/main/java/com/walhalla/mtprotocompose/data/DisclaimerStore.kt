package com.walhalla.mtprotocompose.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class DisclaimerStore(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    fun isAgreed(): Boolean = prefs.getBoolean(KEY_AGREE, false)

    fun setAgreed(agreed: Boolean) {
        prefs.edit { putBoolean(KEY_AGREE, agreed) }
    }

    companion object {
        private const val KEY_AGREE = "licenseagreeok"
    }
}
