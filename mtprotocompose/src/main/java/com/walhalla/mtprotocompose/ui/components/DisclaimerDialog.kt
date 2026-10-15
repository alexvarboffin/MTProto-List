package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.cancel
import com.slapps.cupertino.default
import com.slapps.cupertino.theme.CupertinoTheme
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
        message = {
            CupertinoText(
                text = stringResource(R.string.disclaimer_message),
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState()),
                style = CupertinoTheme.typography.footnote,
            )
        },
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