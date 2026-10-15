package com.walhalla.mtprotocompose.ui.screens

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.slapps.cupertino.CupertinoActionSheet
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.CupertinoIconButton
import com.slapps.cupertino.CupertinoScaffold
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.CupertinoTopAppBar
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.outlined.ArrowClockwise
import com.slapps.cupertino.icons.outlined.ChevronBackward
import com.slapps.cupertino.icons.outlined.Ellipsis
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.util.copyToClipboard
import com.walhalla.ui.DLog.handleException

@OptIn(ExperimentalCupertinoApi::class)
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

    CupertinoScaffold(
        containerColor = CupertinoTheme.colorScheme.systemBackground,
        topBar = {
            Column {
                CupertinoTopAppBar(
                    title = { CupertinoText(stringResource(R.string.webview_title, title)) },
                    navigationIcon = {
                        CupertinoIconButton(onClick = onBack) {
                            CupertinoIcon(CupertinoIcons.Default.ChevronBackward, contentDescription = null)
                        }
                    },
                    actions = {
                        CupertinoIconButton(onClick = {
                            isLoading = true
                            webViewRef?.reload()
                            Toast.makeText(context, R.string.wwPageUpdated, Toast.LENGTH_SHORT).show()
                        }) {
                            CupertinoIcon(CupertinoIcons.Default.ArrowClockwise, contentDescription = null)
                        }
                        CupertinoIconButton(onClick = { showMenu = true }) {
                            CupertinoIcon(CupertinoIcons.Default.Ellipsis, contentDescription = null)
                        }
                    },
                )
                if (isLoading) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        factory = { ctx ->
                            ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
                                isIndeterminate = true
                                progressDrawable = ContextCompat.getDrawable(ctx, R.drawable.progressbar)
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                            }
                        },
                        update = { progressBar ->
                            progressBar.isIndeterminate = true
                            progressBar.visibility = android.view.View.VISIBLE
                        },
                    )
                }
            }
        },
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, startedUrl: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, startedUrl, favicon)
                            isLoading = true
                        }

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
                    webViewRef = this
                }
            },
            update = { webViewRef = it },
        )
    }

    if (showMenu) {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = { showMenu = false },
            buttons = {
                default(onClick = {
                    showMenu = false
                    isLoading = true
                    webViewRef?.loadUrl(homeUrl)
                }) { CupertinoText(stringResource(R.string.wv_menu_home)) }
                default(onClick = {
                    showMenu = false
                    webViewRef?.url?.let { copyToClipboard(context, "url", it) }
                }) { CupertinoText(stringResource(R.string.wv_menu_copy_url)) }
                default(onClick = {
                    showMenu = false
                    clearWebCookies(context)
                }) { CupertinoText(stringResource(R.string.wv_menu_clear_cookies)) }
                default(onClick = {
                    showMenu = false
                    onBack()
                }) { CupertinoText(stringResource(R.string.action_exit)) }
                cancel(onClick = { showMenu = false }) {
                    CupertinoText(stringResource(android.R.string.cancel))
                }
            },
        )
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
