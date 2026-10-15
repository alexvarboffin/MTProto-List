package com.walhalla.mtprotocompose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.walhalla.mtprotocompose.R

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun DisclaimerDialog(
    onAgree: () -> Unit,
    onDismiss: () -> Unit,
) {
    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = { CupertinoText(stringResource(R.string.disclaimer_title)) },
        message = { CupertinoText(stringResource(R.string.disclaimer_message)) },
        buttons = {
            default(onClick = onAgree) {
                CupertinoText(stringResource(R.string.button_agree))
            }
            cancel(onClick = onDismiss) {
                CupertinoText(stringResource(android.R.string.cancel))
            }
        },
    )
}
