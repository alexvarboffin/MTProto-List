package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoButton
import com.slapps.cupertino.CupertinoButtonDefaults
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.theme.CupertinoTheme

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ProxyConnectButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CupertinoButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        colors = CupertinoButtonDefaults.filledButtonColors(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) {
        CupertinoText(
            text = text,
            style = CupertinoTheme.typography.headline,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}