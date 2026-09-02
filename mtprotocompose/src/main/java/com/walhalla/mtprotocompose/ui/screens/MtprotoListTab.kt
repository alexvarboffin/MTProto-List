package com.walhalla.mtprotocompose.ui.screens

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.mtprotocompose.ui.components.EmptyListMessage
import com.walhalla.mtprotocompose.ui.components.InfoBottomSheet
import com.walhalla.mtprotocompose.ui.components.PlayServicesHint
import com.walhalla.mtprotocompose.ui.components.InfoTarget
import com.walhalla.mtprotocompose.ui.components.MtprotoProxyCard
import com.walhalla.mtprotocompose.ui.components.QrCodeDialog
import com.walhalla.mtprotocompose.ui.components.UnlockRewardDialog
import com.walhalla.mtprotocompose.util.copyToClipboard
import com.walhalla.mtprotocompose.util.openTelegramProxy
import com.walhalla.mtprotocompose.util.shareText
import com.walhalla.mtprotocompose.util.showTelegramInstallerDialog
import com.walhalla.mtprotocompose.viewmodel.MtprotoListViewModel
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.utils.AManagerI.RewardManagerCallback
import com.walhalla.utils.RewardManager
import com.walhalla.library.R as WadsR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MtprotoListTab(
    onRefreshRequest: () -> Unit = {},
    onRewardUnlocked: () -> Unit = {},
    viewModel: MtprotoListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val app = context.mtprotoApp()
    val scope = rememberCoroutineScope()

    var infoTarget by remember { mutableStateOf<MtprotoProxy?>(null) }
    var qrContent by remember { mutableStateOf<String?>(null) }
    var viewContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var unlockPosition by remember { mutableIntStateOf(-1) }
    var pendingConnect by remember { mutableStateOf<MtprotoProxy?>(null) }
    var shimmeringIndex by remember { mutableIntStateOf(-1) }

    fun clearConnectShimmer() {
        shimmeringIndex = -1
    }

    fun finishConnectShimmer(index: Int) {
        scope.launch {
            delay(1200)
            if (shimmeringIndex == index) {
                shimmeringIndex = -1
            }
        }
    }

    val rewardCallback = remember {
        object : RewardManagerCallback {
            override fun successResult7(position: Int) {
                app.lockedItemsStore.unlock(position)
                unlockPosition = -1
                onRewardUnlocked()
                pendingConnect?.let { proxy ->
                    openTelegramProxy(context, proxy) {
                        showTelegramInstallerDialog(context)
                    }
                }
                pendingConnect = null
                clearConnectShimmer()
            }

            override fun errorShowAds(position: Int) {
                clearConnectShimmer()
                Toast.makeText(
                    context,
                    context.getString(WadsR.string.ad_not_loaded_try_another_time),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = {
            viewModel.refresh()
            onRefreshRequest()
        },
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
                    itemsIndexed(uiState.items) { index, proxy ->
                        val locked = app.lockedItemsStore.isLocked(index)
                        MtprotoProxyCard(
                            proxy = proxy,
                            position = index,
                            isLocked = locked,
                            isShimmering = shimmeringIndex == index,
                            onConnect = {
                                shimmeringIndex = index
                                if (locked) {
                                    unlockPosition = index
                                    pendingConnect = proxy
                                } else {
                                    openTelegramProxy(context, proxy) {
                                        showTelegramInstallerDialog(context)
                                    }
                                    finishConnectShimmer(index)
                                }
                            },
                            onInfo = { infoTarget = proxy },
                            onView = {
                                viewContent = proxy.host.orEmpty() to proxy.shareUrl()
                            },
                            onShare = {
                                shareText(context, context.getString(R.string.app_name), proxy.shareUrl())
                            },
                            onQr = { qrContent = proxy.shareUrl() },
                            onCopyHost = { copyToClipboard(context, "host", proxy.host.orEmpty()) },
                            onCopyPort = { copyToClipboard(context, "port", proxy.port.orEmpty()) },
                            onCopySecret = { copyToClipboard(context, "secret", proxy.secret.orEmpty()) },
                            onLockClick = {
                                unlockPosition = index
                                pendingConnect = proxy
                            },
                        )
                    }
                }
            }
        }
    }

    infoTarget?.let { proxy ->
        InfoBottomSheet(
            target = InfoTarget.Mtproto(proxy),
            onDismiss = { infoTarget = null },
            onToggleEnabled = {
                scope.launch {
                    app.repository.toggleMtprotoEnabled(proxy).onSuccess { updated ->
                        infoTarget = updated
                        viewModel.refresh()
                    }
                }
            },
            onUpdateGeo = {
                scope.launch {
                    app.repository.refreshMtprotoGeo(proxy).onSuccess { updated ->
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
            title = { Text(stringResource(R.string.dialog_proxy_title, title)) },
            text = { Text(content) },
            confirmButton = {
                TextButton(onClick = { viewContent = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )
    }

    if (unlockPosition >= 0) {
        UnlockRewardDialog(
            onWatchAd = {
                val act = activity
                if (act != null) {
                    RewardManager.instance?.showRewardAdBanner(act, unlockPosition, rewardCallback)
                }
            },
            onDismiss = {
                unlockPosition = -1
                pendingConnect = null
                clearConnectShimmer()
            },
        )
    }
}
