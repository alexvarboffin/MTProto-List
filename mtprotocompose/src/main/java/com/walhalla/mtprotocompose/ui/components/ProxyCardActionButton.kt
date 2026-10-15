package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.filled.SquareAndArrowUp
import com.slapps.cupertino.icons.outlined.Eye
import com.slapps.cupertino.icons.outlined.Qrcode

object ProxyCardActionColors {
    val background = Color.Black
    val info = Color(0xFFDD2C00)
    val view = Color(0xFF2196F3)
    val share = Color(0xFF756B68)
    val qr = Color(0xFF00BCD4)
}

@Composable
fun ProxyCardActionButton(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    iconSize: Dp = 20.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(ProxyCardActionColors.background, RectangleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CupertinoIcon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
fun ProxyCardActionColumn(
    onView: () -> Unit,
    onShare: () -> Unit,
    onQr: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ProxyCardActionButton(
            icon = CupertinoIcons.Default.Eye,
            tint = ProxyCardActionColors.view,
            onClick = onView,
            modifier = Modifier.padding(bottom = 2.dp),
        )
        ProxyCardActionButton(
            icon = CupertinoIcons.Filled.SquareAndArrowUp,
            tint = ProxyCardActionColors.share,
            onClick = onShare,
            modifier = Modifier.padding(bottom = 2.dp),
        )
        ProxyCardActionButton(
            icon = CupertinoIcons.Default.Qrcode,
            tint = ProxyCardActionColors.qr,
            onClick = onQr,
        )
    }
}
