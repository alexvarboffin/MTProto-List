package com.walhalla.mtprotocompose.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.franmontiel.localechanger.LocaleChanger
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotocompose.MainActivity
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.data.LocalePersistor
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.mtprotocompose.recreateForSettingsChange
import com.walhalla.shared.R as SharedR
import com.walhalla.ui.plugins.DialogAbout.aboutDialog
import com.walhalla.ui.plugins.Launcher
import com.walhalla.ui.plugins.Module_U
import android.os.Build
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as? MainActivity
    val app = context.mtprotoApp()
    var nightMode by remember { mutableStateOf(app.settingsRepository.isNightMode()) }
    val langEntries = stringArrayResource(R.array.lang_entries)
    val langValues = stringArrayResource(R.array.lang_values)
    val localeKey = context.resources.configuration.let { config ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales[0].toLanguageTag()
        } else {
            @Suppress("DEPRECATION")
            config.locale.toLanguageTag()
        }
    }
    var selectedLangIndex by remember(localeKey, langValues) {
        mutableIntStateOf(currentLanguageIndex(context, langValues))
    }
    var showLangDialog by remember { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val langCodesSummary = remember(langValues) {
        "(${langValues.joinToString(", ")})"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.action_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.pref_category_lang),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLangDialog = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = stringResource(R.string.pref_title_lang),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = langEntries.getOrElse(selectedLangIndex) { langEntries.first() },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = langCodesSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            HorizontalDivider()

            Text(
                text = stringResource(R.string.pref_category_other),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp),
            )

            SettingsInfoRow(

                title = stringResource(R.string.pref_title_dev_name),

                summary = stringResource(SharedR.string.play_google_pub),

            )

            SettingsInfoRow(

                title = stringResource(R.string.pref_title_copyright),

                summary = stringResource(R.string.pref_summary_copyright, currentYear),

            )

            SettingsRow(stringResource(R.string.action_privacy_policy)) {

                Launcher.openBrowser(context, AppConfig.URL_PRIVACY_POLICY)

            }

            SettingsRow(stringResource(R.string.action_rate_app)) {

                Launcher.rateUs(context)

            }

            SettingsRow(stringResource(R.string.action_about)) {

                aboutDialog(context)

            }

            SettingsRow(stringResource(R.string.pref_title_more)) {

                Module_U.moreApp(context)

            }

            SettingsRow(stringResource(R.string.action_share_app)) {

                Module_U.shareThisApp(context, "")

            }

            SettingsInfoRow(

                title = stringResource(R.string.pref_title_contributors),

                summary = stringResource(R.string.pref_summary_contributors),

            )

            RowWithSwitch(

                title = stringResource(R.string.pref_title_night_mode),

                checked = nightMode,

                onCheckedChange = { enabled ->

                    nightMode = enabled

                    app.settingsRepository.setNightMode(enabled)

                    activity?.recreateForSettingsChange()

                },

            )

        }
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(stringResource(R.string.pref_title_lang)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    langEntries.forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLangIndex = index
                                    val code = langValues.getOrNull(index) ?: return@clickable
                                    persistLanguage(context, code)
                                    LocaleChanger.setLocale(localeFromValue(code))
                                    showLangDialog = false
                                    activity?.recreateForSettingsChange()
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = index == selectedLangIndex,
                                onClick = {
                                    selectedLangIndex = index
                                    val code = langValues.getOrNull(index) ?: return@RadioButton
                                    persistLanguage(context, code)
                                    LocaleChanger.setLocale(localeFromValue(code))
                                    showLangDialog = false
                                    activity?.recreateForSettingsChange()
                                },
                            )
                            Text(text = entry)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}



@Composable
private fun SettingsRow(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
    HorizontalDivider()
}

@Composable
private fun SettingsInfoRow(title: String, summary: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Text(text = summary, style = MaterialTheme.typography.bodySmall)
    }
    HorizontalDivider()
}

@Composable
private fun RowWithSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun persistLanguage(context: android.content.Context, code: String) {
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        .edit()
        .putString(LocalePersistor.KEY_LANGUAGE, code)
        .apply()
}

private fun currentLanguageIndex(context: android.content.Context, langValues: Array<String>): Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    prefs.getString(LocalePersistor.KEY_LANGUAGE, null)?.let { stored ->
        langValues.indexOfFirst { stored.equals(it, ignoreCase = true) }
            .takeIf { it >= 0 }
            ?.let { return it }
    }
    val locale = context.resources.configuration.let { config ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales[0]
        } else {
            @Suppress("DEPRECATION")
            config.locale
        }
    }
    return langValues.indexOfFirst { value -> localeMatchesValue(locale, value) }
        .takeIf { it >= 0 } ?: 0
}

private fun localeMatchesValue(locale: Locale, value: String): Boolean {
    if (value.contains("-")) {
        val language = value.substringBefore("-")
        val region = value.substringAfter("-").removePrefix("r")
        return locale.language.equals(language, ignoreCase = true) &&
            locale.country.equals(region, ignoreCase = true)
    }
    return locale.language.equals(value, ignoreCase = true)
}

private fun localeFromValue(value: String): Locale = when (value) {
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
