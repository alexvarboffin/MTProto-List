package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.default
import com.walhalla.mtprotocompose.R
import net.glxn.qrgen.android.QRCode

private val QrDialogSize = 250.dp

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun QrCodeDialog(
    content: String,
    onDismiss: () -> Unit,
) {
    val bitmap = remember(content) {
        QRCode.from(content).withSize(512, 512).bitmap().asImageBitmap()
    }
    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = { CupertinoText(stringResource(R.string.share_qr_code)) },
        message = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = stringResource(R.string.share_qr_code),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(QrDialogSize),
                )
            }
        },
        buttons = {
            default(onClick = onDismiss) {
                CupertinoText(stringResource(android.R.string.ok))
            }
        },
    )
}
