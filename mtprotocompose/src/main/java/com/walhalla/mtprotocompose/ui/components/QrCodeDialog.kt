package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.walhalla.mtprotocompose.R
import net.glxn.qrgen.android.QRCode

@Composable
fun QrCodeDialog(
    content: String,
    onDismiss: () -> Unit,
) {
    val bitmap = QRCode.from(content).bitmap().asImageBitmap()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.share_qr_code)) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = stringResource(R.string.share_qr_code),
                    modifier = Modifier.padding(8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}
