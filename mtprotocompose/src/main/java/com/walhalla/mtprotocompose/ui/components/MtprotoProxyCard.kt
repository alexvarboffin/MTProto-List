package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoIcon
import com.slapps.cupertino.CupertinoIconButton
import com.slapps.cupertino.CupertinoSurface
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.icons.CupertinoIcons
import com.slapps.cupertino.icons.filled.Lock
import com.slapps.cupertino.icons.filled.InfoCircle
import com.slapps.cupertino.icons.filled.SquareAndArrowUp
import com.slapps.cupertino.icons.outlined.Eye
import com.slapps.cupertino.icons.outlined.Qrcode
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotocompose.R

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun MtprotoProxyCard(
    proxy: MtprotoProxy,
    position: Int,
    isLocked: Boolean,
    onConnect: () -> Unit,
    onInfo: () -> Unit,
    onView: () -> Unit,
    onShare: () -> Unit,
    onQr: () -> Unit,
    onCopyHost: () -> Unit,
    onCopyPort: () -> Unit,
    onCopySecret: () -> Unit,
    onLockClick: () -> Unit,
    isShimmering: Boolean = false,
    modifier: Modifier = Modifier,
) {
    CupertinoSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = CupertinoTheme.colorScheme.secondarySystemBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .proxyCardShimmer(isShimmering)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 8.dp),
            ) {
                CountryFlag(code = proxy.code)
                CupertinoText(
                    text = stringResource(R.string.proxy_port),
                    style = CupertinoTheme.typography.caption2,
                )
                CupertinoText(
                    text = proxy.port.orEmpty(),
                    style = CupertinoTheme.typography.body,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clickable(onClick = onCopyPort),
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CupertinoText(
                        text = proxy.host.orEmpty(),
                        style = CupertinoTheme.typography.headline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .clickable(onClick = onCopyHost),
                    )
                    if (isLocked) {
                        CupertinoIconButton(onClick = onLockClick) {
                            CupertinoIcon(CupertinoIcons.Filled.Lock, contentDescription = null)
                        }
                    }
                }
                CupertinoText(
                    text = proxy.secret.orEmpty(),
                    style = CupertinoTheme.typography.footnote,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onCopySecret),
                )
                ProxyConnectButton(
                    text = stringResource(R.string.action_proxy_connect),
                    onClick = onConnect,
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                ProxyCardActionButton(
                    icon = CupertinoIcons.Filled.InfoCircle,
                    tint = ProxyCardActionColors.info,
                    onClick = onInfo,
                )
                ProxyCardActionButton(
                    icon = CupertinoIcons.Default.Eye,
                    tint = ProxyCardActionColors.view,
                    onClick = onView,
                )
                ProxyCardActionButton(
                    icon = CupertinoIcons.Filled.SquareAndArrowUp,
                    tint = ProxyCardActionColors.share,
                    onClick = onShare,
                )
                ProxyCardActionButton(
                    icon = CupertinoIcons.Default.Qrcode,
                    tint = ProxyCardActionColors.qr,
                    onClick = onQr,
                )
            }
        }
    }
}
