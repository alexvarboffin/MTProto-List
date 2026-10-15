package com.walhalla.mtprotocompose.ui.theme

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.walhalla.mtprotocompose.data.localeFromLanguageCode
import com.walhalla.mtprotocompose.mtprotoApp
import java.util.Locale

/** Keeps Activity as base so startActivity/dialogs work; resources follow selected locale. */
private class LocalizedActivityContext(
    base: Context,
    private val localized: Context,
) : ContextWrapper(base) {
    override fun getResources(): Resources = localized.resources
    override fun getAssets(): AssetManager = localized.assets
}

@Composable
fun ProvideAppLocale(
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val baseLayoutDirection = LocalLayoutDirection.current
    val languageCode by baseContext.mtprotoApp().settingsRepository.languageCode.collectAsState()

    val locale = remember(languageCode) {
        languageCode?.let { localeFromLanguageCode(it) }
    }
    val localizedContext = remember(baseContext, locale) {
        if (locale == null) {
            baseContext
        } else {
            val config = Configuration(baseContext.resources.configuration)
            config.setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(locale))
            }
            val configContext = baseContext.createConfigurationContext(config)
            LocalizedActivityContext(baseContext, configContext)
        }
    }
    val configuration = if (locale == null) {
        baseConfiguration
    } else {
        localizedContext.resources.configuration
    }
    val layoutDirection = remember(locale, baseLayoutDirection) {
        when {
            locale == null -> baseLayoutDirection
            TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
            else -> LayoutDirection.Ltr
        }
    }

    SideEffect {
        locale?.let { Locale.setDefault(it) }
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides configuration,
        LocalLayoutDirection provides layoutDirection,
    ) {
        content()
    }
}