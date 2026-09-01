package com.walhalla.mtprotocompose.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.walhalla.mtprotocompose.R

@Composable
fun DisclaimerDialog(
    onAgree: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.disclaimer_title)) },
        text = {
            Text(
                text = stringResource(R.string.disclaimer_message),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(onClick = onAgree) {
                Text(stringResource(R.string.button_agree))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}
