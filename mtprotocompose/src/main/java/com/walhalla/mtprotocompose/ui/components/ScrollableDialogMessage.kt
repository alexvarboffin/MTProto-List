package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.theme.CupertinoTheme

/** Long alert body: capped height + vertical scroll (Cupertino alerts don't scroll by default). */
@Composable
fun ScrollableDialogMessage(
    text: String,
    modifier: Modifier = Modifier,
    maxHeightFraction: Float = 0.45f,
) {
    val maxHeight = (LocalConfiguration.current.screenHeightDp * maxHeightFraction).dp
    CupertinoText(
        text = text,
        style = CupertinoTheme.typography.footnote,
        modifier = modifier
            .heightIn(max = maxHeight)
            .verticalScroll(rememberScrollState()),
    )
}
