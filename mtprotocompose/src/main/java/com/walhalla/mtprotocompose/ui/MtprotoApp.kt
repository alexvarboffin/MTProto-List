package com.walhalla.mtprotocompose.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.walhalla.mtprotocompose.ui.screens.MainScreen
import com.walhalla.mtprotocompose.ui.screens.SettingsScreen
import com.walhalla.mtprotocompose.ui.screens.SplashScreen
import com.walhalla.mtprotocompose.ui.screens.WebViewScreen
import com.walhalla.mtprotocompose.ui.theme.MtprotoTheme

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val SETTINGS = "settings"
    const val WEBVIEW = "webview?url={url}&title={title}"

    fun webView(url: String, title: String): String {
        val encodedUrl = Uri.encode(url)
        val encodedTitle = Uri.encode(title)
        return "webview?url=$encodedUrl&title=$encodedTitle"
    }
}

@Composable
fun MtprotoApp() {
    MtprotoTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.MAIN) {
                MainScreen(
                    onNavigateSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateWebView = { url, title ->
                        navController.navigate(Routes.webView(url, title))
                    },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.WEBVIEW,
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType },
                    navArgument("title") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) { entry ->
                val url = Uri.decode(entry.arguments?.getString("url").orEmpty())
                val title = Uri.decode(entry.arguments?.getString("title").orEmpty())
                WebViewScreen(
                    url = url,
                    title = title,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
