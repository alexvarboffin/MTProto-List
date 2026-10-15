package com.walhalla.mtprotocompose.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.slapps.cupertino.CupertinoSurface
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.theme.CupertinoTheme
import com.slapps.cupertino.theme.darkColorScheme
import com.slapps.cupertino.theme.lightColorScheme
import com.walhalla.mtprotocompose.mtprotoApp

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun MtprotoTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = context.mtprotoApp().settingsRepository.isNightMode()
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    CupertinoTheme(colorScheme = colorScheme) {
        CupertinoSurface(
            modifier = Modifier.fillMaxSize(),
            color = CupertinoTheme.colorScheme.systemBackground,
            content = content,
        )
    }
}
