package com.walhalla.mtprotocompose.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.slapps.cupertino.CupertinoActionSheet
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.CupertinoIconButton
import com.slapps.cupertino.CupertinoScaffold
import com.slapps.cupertino.CupertinoSegmentedControl
import com.slapps.cupertino.CupertinoSegmentedControlTab
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.CupertinoTopAppBar
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.outlined.ArrowClockwise
import com.slapps.cupertino.icons.outlined.Ellipsis
import com.slapps.cupertino.icons.outlined.ListBullet
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtproto.shared.config.AppConfig
import com.walhalla.mtprotocompose.BuildConfig
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.ui.components.ConfettiOverlay
import com.walhalla.mtprotocompose.viewmodel.MtprotoListViewModel
import com.walhalla.mtprotocompose.viewmodel.WebProxyListViewModel
import com.walhalla.shared.R as SharedR
import com.walhalla.ui.DLog.getAppVersion
import com.walhalla.ui.plugins.DialogAbout.aboutDialog
import com.walhalla.ui.plugins.Launcher
import com.walhalla.ui.plugins.Module_U
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun MainScreen(
    onNavigateSettings: () -> Unit,
    onNavigateWebView: (url: String, title: String) -> Unit,
    mtprotoViewModel: MtprotoListViewModel = viewModel(),
    webProxyViewModel: WebProxyListViewModel = viewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    var showDrawerMenu by remember { mutableStateOf(false) }
    var showOverflow by remember { mutableStateOf(false) }
    var showMirror by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }
    var confettiTrigger by remember { mutableIntStateOf(0) }
    var backPressStep by remember { mutableIntStateOf(0) }

    val mtprotoState by mtprotoViewModel.uiState.collectAsStateWithLifecycle()
    val webProxyState by webProxyViewModel.uiState.collectAsStateWithLifecycle()

    val pressAgainText = stringResource(SharedR.string.press_again_to_exit)
    val dataUpdatedText = stringResource(R.string.data_successfully_updated)
    val tabMtproto = stringResource(R.string.tab_mtproto)
    val tabWebProxy = stringResource(R.string.tab_webproxy)

    LaunchedEffect(mtprotoState.successMessage) {
        mtprotoState.successMessage?.let {
            alertMessage = dataUpdatedText
            mtprotoViewModel.consumeSuccessMessage()
        }
    }
    LaunchedEffect(webProxyState.successMessage) {
        webProxyState.successMessage?.let {
            alertMessage = dataUpdatedText
            webProxyViewModel.consumeSuccessMessage()
        }
    }
    LaunchedEffect(mtprotoState.error) {
        mtprotoState.error?.let { alertMessage = it }
    }
    LaunchedEffect(webProxyState.error) {
        webProxyState.error?.let { alertMessage = it }
    }

    BackHandler {
        if (pagerState.currentPage != 0) {
            scope.launch { pagerState.animateScrollToPage(0) }
        } else if (backPressStep > 0) {
            (context as? Activity)?.moveTaskToBack(true)
        } else {
            backPressStep = 1
            alertMessage = pressAgainText
            scope.launch {
                delay(1200)
                backPressStep = 0
                if (alertMessage == pressAgainText) alertMessage = null
            }
        }
    }

    val mirrorSites = listOf(
        "https://mtprotolist.blogspot.com",
        "https://protoproxy.blogspot.com",
        "https://proxy-telegram.blogspot.com",
        "https://mtproton.blogspot.com",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CupertinoScaffold(
            containerColor = CupertinoTheme.colorScheme.systemBackground,
            topBar = {
                CupertinoTopAppBar(
                    title = {
                        Column {
                            CupertinoText(stringResource(R.string.app_name))
                            CupertinoText(
                                text = getAppVersion(context),
                                style = CupertinoTheme.typography.caption2,
                            )
                        }
                    },
                    navigationIcon = {
                        CupertinoIconButton(onClick = { showDrawerMenu = true }) {
                            CupertinoIcon(CupertinoIcons.Default.ListBullet, contentDescription = null)
                        }
                    },
                    actions = {
                        CupertinoIconButton(
                            onClick = {
                                if (pagerState.currentPage == 0) {
                                    mtprotoViewModel.refresh()
                                } else {
                                    webProxyViewModel.refresh()
                                }
                            },
                        ) {
                            CupertinoIcon(CupertinoIcons.Default.ArrowClockwise, contentDescription = null)
                        }
                        CupertinoIconButton(onClick = { showOverflow = true }) {
                            CupertinoIcon(CupertinoIcons.Default.Ellipsis, contentDescription = null)
                        }
                    },
                )
            },
            bottomBar = {
                BannerAd(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                CupertinoSegmentedControl(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CupertinoSegmentedControlTab(
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        isSelected = pagerState.currentPage == 0,
                    ) {
                        CupertinoText(tabMtproto)
                    }
                    CupertinoSegmentedControlTab(
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        isSelected = pagerState.currentPage == 1,
                    ) {
                        CupertinoText(tabWebProxy)
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> MtprotoListTab(
                            viewModel = mtprotoViewModel,
                            onRewardUnlocked = { confettiTrigger++ },
                        )
                        1 -> WebProxyListTab(
                            viewModel = webProxyViewModel,
                            onOpenWebView = onNavigateWebView,
                        )
                    }
                }
            }
        }

        ConfettiOverlay(trigger = confettiTrigger)
    }

    if (showDrawerMenu) {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = { showDrawerMenu = false },
            title = { CupertinoText(stringResource(R.string.app_name)) },
            buttons = {
                default(onClick = {
                    showDrawerMenu = false
                    onNavigateSettings()
                }) {
                    CupertinoText(stringResource(R.string.action_settings))
                }
                default(onClick = {
                    showDrawerMenu = false
                    Module_U.shareThisApp(context, "")
                }) {
                    CupertinoText(stringResource(R.string.action_share_app))
                }
                cancel(onClick = { showDrawerMenu = false }) {
                    CupertinoText(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    if (showOverflow) {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = { showOverflow = false },
            buttons = {
                default(onClick = {
                    showOverflow = false
                    aboutDialog(context)
                }) { CupertinoText(stringResource(R.string.action_about)) }
                default(onClick = {
                    showOverflow = false
                    onNavigateSettings()
                }) { CupertinoText(stringResource(R.string.action_settings)) }
                default(onClick = {
                    showOverflow = false
                    Launcher.openBrowser(context, AppConfig.URL_PRIVACY_POLICY)
                }) { CupertinoText(stringResource(R.string.action_privacy_policy)) }
                default(onClick = {
                    showOverflow = false
                    Launcher.rateUs(context)
                }) { CupertinoText(stringResource(R.string.action_rate_app)) }
                default(onClick = {
                    showOverflow = false
                    Module_U.shareThisApp(context, "")
                }) { CupertinoText(stringResource(R.string.action_share_app)) }
                default(onClick = {
                    showOverflow = false
                    Module_U.moreApp(context)
                }) { CupertinoText(stringResource(R.string.action_discover_more_app)) }
                default(onClick = {
                    showOverflow = false
                    showMirror = true
                }) { CupertinoText(stringResource(R.string.action_mirror)) }
                cancel(onClick = { showOverflow = false }) {
                    CupertinoText(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    if (showMirror) {
        CupertinoActionSheet(
            visible = true,
            onDismissRequest = { showMirror = false },
            title = { CupertinoText(stringResource(R.string.action_mirror)) },
            buttons = {
                mirrorSites.forEach { site ->
                    default(onClick = {
                        showMirror = false
                        Launcher.openBrowser(context, site)
                    }) {
                        CupertinoText(site)
                    }
                }
                cancel(onClick = { showMirror = false }) {
                    CupertinoText(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    alertMessage?.let { message ->
        CupertinoAlertDialog(
            onDismissRequest = { alertMessage = null },
            title = { CupertinoText(stringResource(R.string.app_name)) },
            message = { CupertinoText(message) },
            buttons = {
                default(onClick = { alertMessage = null }) {
                    CupertinoText(stringResource(android.R.string.ok))
                }
            },
        )
    }

}

@Composable
private fun BannerAd(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var adViewHolder by remember { mutableStateOf<AdView?>(null) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = ctx.getString(R.string.b1)
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(
                                ctx,
                                "Banner ad: ${error.code} ${error.message}",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                }
                loadAd(AdRequest.Builder().build())
                adViewHolder = this
            }
        },
        update = { adViewHolder = it },
    )
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val view = adViewHolder ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_PAUSE -> view.pause()
                Lifecycle.Event.ON_RESUME -> view.resume()
                Lifecycle.Event.ON_DESTROY -> view.destroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adViewHolder?.destroy()
        }
    }
}
