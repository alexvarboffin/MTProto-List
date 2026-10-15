package com.walhalla.mtprotocompose.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.franmontiel.localechanger.LocaleChanger
import com.slapps.cupertino.CupertinoActionSheet
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.CupertinoIconButton
import com.slapps.cupertino.CupertinoScaffold
import com.slapps.cupertino.CupertinoSwitch
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.CupertinoTopAppBar
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.outlined.ChevronBackward
import com.slapps.cupertino.section.CupertinoSection
import com.slapps.cupertino.section.SectionItem
import com.slapps.cupertino.section.SectionLink
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.data.localeFromLanguageCode
import com.walhalla.mtprotocompose.data.localeMatchesLanguageCode
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.shared.R as SharedR
import com.walhalla.ui.plugins.DialogAbout.aboutDialog
import com.walhalla.ui.plugins.Launcher
import com.walhalla.ui.plugins.Module_U
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val app = context.mtprotoApp()
    val nightMode by app.settingsRepository.nightMode.collectAsState()
    val languageCode by app.settingsRepository.languageCode.collectAsState()
    val langEntries = stringArrayResource(R.array.lang_entries)
    val langValues = stringArrayResource(R.array.lang_values)
    val selectedLangIndex = remember(languageCode, langValues, configuration) {
        currentLanguageIndex(languageCode, langValues, configuration)
    }
    var showLangDialog by remember { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    CupertinoScaffold(
        containerColor = CupertinoTheme.colorScheme.systemBackground,
        topBar = {
            CupertinoTopAppBar(
                title = { CupertinoText(stringResource(R.string.action_settings)) },
                navigationIcon = {
                    CupertinoIconButton(onClick = onBack) {
                        CupertinoIcon(CupertinoIcons.Default.ChevronBackward, contentDescription = null)
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
            CupertinoSection(
                title = { CupertinoText(stringResource(R.string.pref_category_lang)) },
            ) {
                SectionLink(
                    onClick = { showLangDialog = true },
                    caption = {
                        CupertinoText(
                            langEntries.getOrElse(selectedLangIndex) { langEntries.first() },
                        )
                    },
                    title = { CupertinoText(stringResource(R.string.pref_title_lang)) },
                )
            }

            CupertinoSection(
                title = { CupertinoText(stringResource(R.string.pref_category_other)) },
            ) {
                SectionItem(
                    title = { CupertinoText(stringResource(R.string.pref_title_dev_name)) },
                    trailingContent = {
                        CupertinoText(
                            stringResource(SharedR.string.play_google_pub),
                            style = CupertinoTheme.typography.footnote,
                        )
                    },
                )
                SectionItem(
                    title = { CupertinoText(stringResource(R.string.pref_title_copyright)) },
                    trailingContent = {
                        CupertinoText(
                            stringResource(R.string.pref_summary_copyright, currentYear),
                            style = CupertinoTheme.typography.footnote,
                        )
                    },
                )
                SectionLink(
                    onClick = { Launcher.openBrowser(context, AppConfig.URL_PRIVACY_POLICY) },
                    title = { CupertinoText(stringResource(R.string.action_privacy_policy)) },
                )
                SectionLink(
                    onClick = { Launcher.rateUs(context) },
                    title = { CupertinoText(stringResource(R.string.action_rate_app)) },
                )
                SectionLink(
                    onClick = { aboutDialog(context) },
                    title = { CupertinoText(stringResource(R.string.action_about)) },
                )
                SectionLink(
                    onClick = { Module_U.moreApp(context) },
                    title = { CupertinoText(stringResource(R.string.pref_title_more)) },
                )
                SectionLink(
                    onClick = { Module_U.shareThisApp(context, "") },
                    title = { CupertinoText(stringResource(R.string.action_share_app)) },
                )
                SectionItem(
                    title = { CupertinoText(stringResource(R.string.pref_title_contributors)) },
                    trailingContent = {
                        CupertinoText(
                            stringResource(R.string.pref_summary_contributors),
                            style = CupertinoTheme.typography.footnote,
                        )
                    },
                )
                SectionItem(
                    title = { CupertinoText(stringResource(R.string.pref_title_night_mode)) },
                    trailingContent = {
                        CupertinoSwitch(
                            checked = nightMode,
                            onCheckedChange = { enabled ->
                                app.settingsRepository.setNightMode(enabled)
                            },
                        )
                    },
                )
            }
        }
    }

    if (showLangDialog) {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = { showLangDialog = false },
            title = { CupertinoText(stringResource(R.string.pref_title_lang)) },
            buttons = {
                langEntries.forEachIndexed { index, entry ->
                    default(
                        onClick = {
                            val code = langValues.getOrNull(index) ?: return@default
                            val locale = localeFromLanguageCode(code)
                            app.settingsRepository.setLanguageCode(code)
                            LocaleChanger.setLocale(locale)
                            showLangDialog = false
                        },
                    ) {
                        CupertinoText(
                            if (index == selectedLangIndex) "✓ $entry" else entry,
                        )
                    }
                }
                cancel(onClick = { showLangDialog = false }) {
                    CupertinoText(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

private fun currentLanguageIndex(
    languageCode: String?,
    langValues: Array<String>,
    configuration: Configuration,
): Int {
    languageCode?.let { stored ->
        langValues.indexOfFirst { stored.equals(it, ignoreCase = true) }
            .takeIf { it >= 0 }
            ?.let { return it }
    }
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
    return langValues.indexOfFirst { value -> localeMatchesLanguageCode(locale, value) }
        .takeIf { it >= 0 } ?: 0
}
