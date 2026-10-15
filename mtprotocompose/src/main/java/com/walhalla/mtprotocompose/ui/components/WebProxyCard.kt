package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoSurface
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.mtprotocompose.R

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun WebProxyCard(
    proxy: ProxyInfo,
    onConnect: () -> Unit,
    onInfo: () -> Unit,
    onView: () -> Unit,
    onShare: () -> Unit,
    onQr: () -> Unit,
    onCopyIp: () -> Unit = {},
    onCopyUrl: () -> Unit = {},
    onCopyType: () -> Unit = {},
    isShimmering: Boolean = false,
    modifier: Modifier = Modifier,
) {
    CupertinoSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = CupertinoTheme.colorScheme.secondarySystemBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .proxyCardShimmer(isShimmering)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 8.dp),
            ) {
                ProxyFlagInfoButton(
                    code = proxy.code,
                    onClick = onInfo,
                )
                CupertinoText(
                    text = stringResource(R.string.proxy_type_label),
                    style = CupertinoTheme.typography.caption2,
                )
                CupertinoText(
                    text = proxy.type.orEmpty(),
                    style = CupertinoTheme.typography.body,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clickable(onClick = onCopyType),
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                CupertinoText(
                    text = proxy.ip.orEmpty(),
                    style = CupertinoTheme.typography.headline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onCopyIp),
                )
                CupertinoText(
                    text = proxy.proxyUrl.orEmpty(),
                    style = CupertinoTheme.typography.footnote,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onCopyUrl),
                )
                ProxyConnectButton(
                    text = stringResource(R.string.action_glype_connect),
                    onClick = onConnect,
                )
            }
            ProxyCardActionColumn(
                onView = onView,
                onShare = onShare,
                onQr = onQr,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}
