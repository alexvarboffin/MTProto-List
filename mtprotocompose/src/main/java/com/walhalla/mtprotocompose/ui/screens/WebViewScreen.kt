package com.walhalla.mtprotocompose.ui.screens

import android.os.Build
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.util.copyToClipboard
import com.walhalla.ui.DLog.handleException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var cssInjected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    val homeUrl = remember(url) { url }

    BackHandler {
        val webView = webViewRef
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.webview_title, title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webViewRef?.reload()
                        Toast.makeText(context, R.string.wwPageUpdated, Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.wv_menu_home)) },
                            onClick = {
                                showMenu = false
                                webViewRef?.loadUrl(homeUrl)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.wv_menu_copy_url)) },
                            onClick = {
                                showMenu = false
                                webViewRef?.url?.let { copyToClipboard(context, "url", it) }
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.wv_menu_clear_cookies)) },
                            onClick = {
                                showMenu = false
                                clearWebCookies(context)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_exit)) },
                            onClick = {
                                showMenu = false
                                onBack()
                            },
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, finishedUrl: String?) {
                            super.onPageFinished(view, finishedUrl)
                            isLoading = false
                            if (!cssInjected && view != null) {
                                injectCss(view, context.assets.open("style.css"))
                                cssInjected = true
                            }
                        }
                    }
                    loadUrl(url)
                    isLoading = true
                    webViewRef = this
                }
            },
            update = { webViewRef = it },
            )
        }
    }
}

private fun injectCss(webView: WebView, inputStream: java.io.InputStream) {
    try {
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
        webView.loadUrl(
            "javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = window.atob('$encoded');" +
                "parent.appendChild(style)" +
                "})()",
        )
    } catch (e: Exception) {
        handleException(e)
    }
}

private fun clearWebCookies(context: android.content.Context) {
    val cookieManager = CookieManager.getInstance()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    } else {
        CookieSyncManager.createInstance(context)
        CookieManager.getInstance().removeAllCookie()
    }
}
