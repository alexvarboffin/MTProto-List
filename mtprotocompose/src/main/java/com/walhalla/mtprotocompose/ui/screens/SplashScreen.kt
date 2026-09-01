package com.walhalla.mtprotocompose.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.mtprotoApp
import com.walhalla.ui.DLog
import com.walhalla.ui.SharedPref
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val app = context.mtprotoApp()

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = DLog.getAppVersion(context),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}
