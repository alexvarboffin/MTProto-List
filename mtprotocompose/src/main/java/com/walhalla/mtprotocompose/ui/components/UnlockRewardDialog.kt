package com.walhalla.mtprotocompose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.walhalla.library.R as WadsR

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun UnlockRewardDialog(
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit,
) {
    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = { CupertinoText(stringResource(WadsR.string.watch_reward_video_ad_to_unlock)) },
        buttons = {
            default(onClick = onWatchAd) {
                CupertinoText(stringResource(WadsR.string.watch_ad))
            }
            cancel(onClick = onDismiss) {
                CupertinoText(stringResource(android.R.string.cancel))
            }
        },
    )
}
