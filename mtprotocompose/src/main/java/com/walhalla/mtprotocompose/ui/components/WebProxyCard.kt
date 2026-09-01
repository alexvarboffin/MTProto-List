package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
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
import com.walhalla.mtprotolist.webproxy.ProxyInfo
import com.walhalla.mtprotocompose.R

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
                    text = stringResource(R.string.proxy_type_label),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = proxy.type.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clickable(onClick = onCopyType),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = proxy.ip.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onCopyIp),
                )
                Text(
                    text = proxy.proxyUrl.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable(onClick = onCopyUrl),
                )
                Button(onClick = onConnect, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.action_glype_connect))
                }
            }
            Column {
                IconButton(onClick = onInfo) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
                IconButton(onClick = onView) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null)
                }
                IconButton(onClick = onQr) {
                    Icon(Icons.Default.QrCode, contentDescription = null)
                }
            }
        }
    }
}
