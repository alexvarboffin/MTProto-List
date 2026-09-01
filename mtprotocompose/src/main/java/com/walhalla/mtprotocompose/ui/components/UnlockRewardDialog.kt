package com.walhalla.mtprotocompose.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.walhalla.library.R as WadsR

@Composable
fun UnlockRewardDialog(
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(WadsR.string.watch_reward_video_ad_to_unlock)) },
        confirmButton = {
            Button(onClick = onWatchAd) {
                Text(stringResource(WadsR.string.watch_ad))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}
