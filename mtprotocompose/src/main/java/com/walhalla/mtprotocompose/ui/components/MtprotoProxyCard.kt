package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotocompose.R

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
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 8.dp),
            ) {
                CountryFlag(code = proxy.code)
                Text(
                    text = stringResource(R.string.proxy_port),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = proxy.port.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
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
                    Text(
                        text = proxy.host.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .clickable(onClick = onCopyHost),
                    )
                    if (isLocked) {
                        IconButton(onClick = onLockClick) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        }
                    }
                }
                Text(
                    text = proxy.secret.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
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
                    icon = Icons.Default.Info,
                    tint = ProxyCardActionColors.info,
                    onClick = onInfo,
                )
                ProxyCardActionButton(
                    icon = Icons.Default.Visibility,
                    tint = ProxyCardActionColors.view,
                    onClick = onView,
                )
                ProxyCardActionButton(
                    icon = Icons.Default.Share,
                    tint = ProxyCardActionColors.share,
                    onClick = onShare,
                )
                ProxyCardActionButton(
                    icon = Icons.Default.QrCode,
                    tint = ProxyCardActionColors.qr,
                    onClick = onQr,
                )
            }
        }
    }
}
