package com.walhalla.mtprotocompose.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.mtprotocompose.ui.components.DisclaimerDialog
import com.walhalla.mtprotocompose.ui.components.EmptyListMessage
import com.walhalla.mtprotocompose.ui.components.InfoBottomSheet
import com.walhalla.mtprotocompose.ui.components.PlayServicesHint
import com.walhalla.mtprotocompose.ui.components.InfoTarget
import com.walhalla.mtprotocompose.ui.components.QrCodeDialog
import com.walhalla.mtprotocompose.ui.components.WebProxyCard
import com.walhalla.mtprotocompose.util.copyToClipboard
import com.walhalla.mtprotocompose.util.shareText
import com.walhalla.mtprotocompose.viewmodel.WebProxyListViewModel
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebProxyListTab(
    onOpenWebView: (url: String, title: String) -> Unit,
    viewModel: WebProxyListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val app = context.mtprotoApp()
    val scope = rememberCoroutineScope()

    var infoTarget by remember { mutableStateOf<ProxyInfo?>(null) }
    var qrContent by remember { mutableStateOf<String?>(null) }
    var viewContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var pendingProxy by remember { mutableStateOf<ProxyInfo?>(null) }
    var showDisclaimer by remember { mutableStateOf(false) }

    fun openProxy(proxy: ProxyInfo) {
        val url = proxy.proxyUrl.orEmpty()
        if (url.isBlank()) return
        if (app.disclaimerStore.isAgreed()) {
            onOpenWebView(url, proxy.type.orEmpty())
        } else {
            pendingProxy = proxy
            showDisclaimer = true
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            uiState.isLoading && uiState.items.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null && uiState.items.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error.orEmpty(), modifier = Modifier.padding(16.dp))
                }
            }
            uiState.items.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EmptyListMessage()
                        PlayServicesHint(modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.items) { proxy ->
                        WebProxyCard(
                            proxy = proxy,
                            onConnect = { openProxy(proxy) },
                            onInfo = { infoTarget = proxy },
                            onView = {
                                viewContent = proxy.ip.orEmpty() to proxy.proxyUrl.orEmpty()
                            },
                            onShare = {
                                shareText(
                                    context,
                                    context.getString(R.string.app_name),
                                    proxy.proxyUrl.orEmpty(),
                                )
                            },
                            onQr = { qrContent = proxy.proxyUrl.orEmpty() },
                            onCopyIp = { copyToClipboard(context, "ip", proxy.ip.orEmpty()) },
                            onCopyUrl = { copyToClipboard(context, "url", proxy.proxyUrl.orEmpty()) },
                            onCopyType = { copyToClipboard(context, "type", proxy.type.orEmpty()) },
                        )
                    }
                }
            }
        }
    }

    infoTarget?.let { proxy ->
        InfoBottomSheet(
            target = InfoTarget.Web(proxy),
            onDismiss = { infoTarget = null },
            onToggleEnabled = {
                scope.launch {
                    app.repository.toggleWebProxyEnabled(proxy).onSuccess { updated ->
                        infoTarget = updated
                        viewModel.refresh()
                    }
                }
            },
            onUpdateGeo = {
                scope.launch {
                    app.repository.refreshWebProxyGeo(proxy).onSuccess { updated ->
                        infoTarget = updated
                        viewModel.refresh()
                    }
                }
            },
        )
    }

    qrContent?.let { content ->
        QrCodeDialog(content = content, onDismiss = { qrContent = null })
    }

    viewContent?.let { (title, content) ->
        AlertDialog(
            onDismissRequest = { viewContent = null },
            title = { Text(stringResource(R.string.dialog_webproxy_title, title)) },
            text = { Text(content) },
            confirmButton = {
                TextButton(onClick = { viewContent = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )
    }

    if (showDisclaimer) {
        DisclaimerDialog(
            onAgree = {
                app.disclaimerStore.setAgreed(true)
                showDisclaimer = false
                pendingProxy?.let { openProxy(it) }
                pendingProxy = null
            },
            onDismiss = {
                showDisclaimer = false
                pendingProxy = null
            },
        )
    }
}
