package com.walhalla.mtprotocompose.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
    val darkTheme by context.mtprotoApp().settingsRepository.nightMode.collectAsState()
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CupertinoTheme(colorScheme = colorScheme) {
        CupertinoSurface(
            modifier = Modifier.fillMaxSize(),
            color = CupertinoTheme.colorScheme.systemBackground,
            content = content,
        )
    }
}
