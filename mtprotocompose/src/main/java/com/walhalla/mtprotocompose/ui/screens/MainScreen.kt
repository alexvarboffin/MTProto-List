package com.walhalla.mtprotocompose.ui.screens



import android.app.Activity

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.pager.HorizontalPager

import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Menu

import androidx.compose.material.icons.filled.MoreVert

import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material3.AlertDialog

import androidx.compose.material3.DrawerValue

import androidx.compose.material3.DropdownMenu

import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet

import androidx.compose.material3.ModalNavigationDrawer

import androidx.compose.material3.NavigationDrawerItem

import androidx.compose.material3.Scaffold

import androidx.compose.material3.SnackbarHost

import androidx.compose.material3.SnackbarHostState

import androidx.compose.material3.Tab

import androidx.compose.material3.TabRow

import androidx.compose.material3.Text

import androidx.compose.material3.TextButton

import androidx.compose.material3.TopAppBar

import androidx.compose.material3.rememberDrawerState

import androidx.compose.runtime.Composable

import androidx.compose.runtime.DisposableEffect

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

import com.google.android.gms.ads.AdRequest

import com.google.android.gms.ads.AdSize

import com.google.android.gms.ads.AdView

import com.walhalla.mtproto.shared.config.AppConfig

import com.walhalla.mtprotocompose.BuildConfig

import com.walhalla.mtprotocompose.R

import com.walhalla.mtprotocompose.ui.components.ConfettiOverlay

import com.walhalla.mtprotocompose.util.showTelegramInstallerDialog

import com.walhalla.mtprotocompose.viewmodel.MtprotoListViewModel

import com.walhalla.mtprotocompose.viewmodel.WebProxyListViewModel

import com.walhalla.shared.R as SharedR

import com.walhalla.ui.DLog.getAppVersion

import com.walhalla.ui.plugins.DialogAbout.aboutDialog

import com.walhalla.ui.plugins.Launcher

import com.walhalla.ui.plugins.Module_U

import kotlinx.coroutines.delay

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun MainScreen(

    onNavigateSettings: () -> Unit,

    onNavigateWebView: (url: String, title: String) -> Unit,

    mtprotoViewModel: MtprotoListViewModel = viewModel(),

    webProxyViewModel: WebProxyListViewModel = viewModel(),

) {

    val context = LocalContext.current

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    val snackbarHostState = remember { SnackbarHostState() }

    var showOverflow by remember { mutableStateOf(false) }

    var showMirror by remember { mutableStateOf(false) }

    var confettiTrigger by remember { mutableIntStateOf(0) }

    var backPressStep by remember { mutableIntStateOf(0) }



    val mtprotoState by mtprotoViewModel.uiState.collectAsStateWithLifecycle()

    val webProxyState by webProxyViewModel.uiState.collectAsStateWithLifecycle()



    val pressAgainText = stringResource(SharedR.string.press_again_to_exit)

    val dataUpdatedText = stringResource(R.string.data_successfully_updated)



    androidx.compose.runtime.LaunchedEffect(mtprotoState.successMessage) {

        mtprotoState.successMessage?.let {

            snackbarHostState.showSnackbar(dataUpdatedText)

            mtprotoViewModel.consumeSuccessMessage()

        }

    }

    androidx.compose.runtime.LaunchedEffect(webProxyState.successMessage) {

        webProxyState.successMessage?.let {

            snackbarHostState.showSnackbar(dataUpdatedText)

            webProxyViewModel.consumeSuccessMessage()

        }

    }

    androidx.compose.runtime.LaunchedEffect(mtprotoState.error) {

        mtprotoState.error?.let { snackbarHostState.showSnackbar(it) }

    }

    androidx.compose.runtime.LaunchedEffect(webProxyState.error) {

        webProxyState.error?.let { snackbarHostState.showSnackbar(it) }

    }



    BackHandler {

        if (pagerState.currentPage != 0) {

            scope.launch { pagerState.animateScrollToPage(0) }

        } else if (backPressStep > 0) {

            (context as? Activity)?.moveTaskToBack(true)

        } else {

            backPressStep = 1

            scope.launch {

                snackbarHostState.showSnackbar(pressAgainText)

                delay(1200)

                backPressStep = 0

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

        ModalNavigationDrawer(

            drawerState = drawerState,

            drawerContent = {

                ModalDrawerSheet {

                    Text(

                        text = stringResource(R.string.app_name),

                        modifier = Modifier.padding(16.dp),

                    )

                    HorizontalDivider()

                    NavigationDrawerItem(

                        label = { Text(stringResource(R.string.action_settings)) },

                        selected = false,

                        onClick = {

                            scope.launch { drawerState.close() }

                            onNavigateSettings()

                        },

                    )

                    NavigationDrawerItem(

                        label = { Text(stringResource(R.string.action_share_app)) },

                        selected = false,

                        onClick = {

                            scope.launch { drawerState.close() }

                            Module_U.shareThisApp(context, "")

                        },

                    )

                }

            },

        ) {

            Scaffold(

                snackbarHost = { SnackbarHost(snackbarHostState) },

                topBar = {

                    TopAppBar(

                        title = {

                            Column(

                                modifier = Modifier.clickable {

                                    if (BuildConfig.DEBUG) {

                                        showTelegramInstallerDialog(context)

                                    } else {

                                        aboutDialog(context)

                                    }

                                },

                            ) {

                                Text(stringResource(R.string.app_name))

                                Text(

                                    text = getAppVersion(context),

                                    style = MaterialTheme.typography.labelSmall,

                                )

                            }

                        },

                        navigationIcon = {

                            IconButton(onClick = {

                                scope.launch { drawerState.open() }

                            }) {

                                Icon(Icons.Default.Menu, contentDescription = null)

                            }

                        },

                        actions = {

                            IconButton(onClick = {

                                if (pagerState.currentPage == 0) {

                                    mtprotoViewModel.refresh()

                                } else {

                                    webProxyViewModel.refresh()

                                }

                            }) {

                                Icon(Icons.Default.Refresh, contentDescription = null)

                            }

                            IconButton(onClick = { showOverflow = true }) {

                                Icon(Icons.Default.MoreVert, contentDescription = null)

                            }

                            DropdownMenu(

                                expanded = showOverflow,

                                onDismissRequest = { showOverflow = false },

                            ) {

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_about)) },

                                    onClick = {

                                        showOverflow = false

                                        aboutDialog(context)

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_settings)) },

                                    onClick = {

                                        showOverflow = false

                                        onNavigateSettings()

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_privacy_policy)) },

                                    onClick = {

                                        showOverflow = false

                                        Launcher.openBrowser(context, AppConfig.URL_PRIVACY_POLICY)

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_rate_app)) },

                                    onClick = {

                                        showOverflow = false

                                        Launcher.rateUs(context)

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_share_app)) },

                                    onClick = {

                                        showOverflow = false

                                        Module_U.shareThisApp(context, "")

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_discover_more_app)) },

                                    onClick = {

                                        showOverflow = false

                                        Module_U.moreApp(context)

                                    },

                                )

                                DropdownMenuItem(

                                    text = { Text(stringResource(R.string.action_mirror)) },

                                    onClick = {

                                        showOverflow = false

                                        showMirror = true

                                    },

                                )

                            }

                        },

                    )

                },

                bottomBar = {

                    BannerAd(modifier = Modifier.fillMaxWidth())

                },

            ) { padding ->

                Column(

                    modifier = Modifier

                        .fillMaxSize()

                        .padding(padding),

                ) {

                    TabRow(selectedTabIndex = pagerState.currentPage) {

                        Tab(

                            selected = pagerState.currentPage == 0,

                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },

                            text = { Text(stringResource(R.string.tab_mtproto)) },

                        )

                        Tab(

                            selected = pagerState.currentPage == 1,

                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },

                            text = { Text(stringResource(R.string.tab_webproxy)) },

                        )

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

        }



        ConfettiOverlay(trigger = confettiTrigger)

    }



    if (showMirror) {

        AlertDialog(

            onDismissRequest = { showMirror = false },

            title = { Text(stringResource(R.string.action_mirror)) },

            text = {

                Column {

                    mirrorSites.forEach { site ->

                        Text(

                            text = site,

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable {

                                    showMirror = false

                                    Launcher.openBrowser(context, site)

                                }

                                .padding(vertical = 8.dp),

                        )

                    }

                }

            },

            confirmButton = {

                TextButton(onClick = { showMirror = false }) {

                    Text(stringResource(android.R.string.cancel))

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


