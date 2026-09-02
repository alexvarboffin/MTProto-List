package com.walhalla.mtprotocompose.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.ui.DLog
import com.walhalla.ui.SharedPref
import kotlinx.coroutines.delay
import pl.bclogic.pulsator4droid.library.PulsatorLayout

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val app = context.mtprotoApp()
    val view = LocalView.current
    val pulsatorStarted = remember { booleanArrayOf(false) }

    DisposableEffect(Unit) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            onDispose {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(Unit) {
        val pref = SharedPref.getInstance(context)
        val launchCount = pref.launchCount
        val requestAds = when {
            launchCount == 0 -> {
                pref.launchCount = 1
                false
            }
            launchCount == 1 -> {
                pref.launchCount = 2
                true
            }
            else -> true
        }
        delay(1000)
        if (requestAds && activity != null) {
            app.showAdIfAvailable(activity) { onFinished() }
        } else {
            onFinished()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ic_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )

        AndroidView(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 19.dp)
                .width(326.dp)
                .height(446.dp),
            factory = { ctx ->
                LayoutInflater.from(ctx)
                    .inflate(R.layout.splash_pulsator, null, false)
                    .apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }
            },
            update = { root ->
                if (!pulsatorStarted[0]) {
                    root.findViewById<PulsatorLayout>(R.id.pulsator)?.apply {
                        count = 3
                        duration = 2200
                        start()
                    }
                    pulsatorStarted[0] = true
                }
            },
        )

        Text(
            text = DLog.getAppVersion(context),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
        )
    }
}
