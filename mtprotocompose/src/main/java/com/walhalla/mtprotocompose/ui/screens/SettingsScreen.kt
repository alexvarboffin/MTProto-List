package com.walhalla.mtprotocompose.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

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
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.pref_category_lang),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp),
            )
            langEntries.forEachIndexed { index, entry ->
                Text(
                    text = entry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val code = langValues.getOrNull(index) ?: return@clickable
                            LocaleChanger.setLocale(Locale(code))
                            activity?.recreateForSettingsChange()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
                if (index < langEntries.lastIndex) {
                    HorizontalDivider()
                }
            }
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
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
