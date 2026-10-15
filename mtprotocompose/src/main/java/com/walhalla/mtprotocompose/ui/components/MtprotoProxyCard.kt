package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.util.formatRelativeUpdatedAt
import com.walhalla.mtprotocompose.util.tcpPingMs
import com.walhalla.mtprotolist.entity.MtprotoProxy

private sealed interface PingUi {
    data object Measuring : PingUi
    data class Ready(val ms: Long) : PingUi
    data object Timeout : PingUi
}

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
    val city = proxy.city.orEmpty().trim()
    val isp = proxy.isp.orEmpty().trim()
    val metaLine = when {
        city.isNotEmpty() && isp.isNotEmpty() ->
            stringResource(R.string.proxy_meta_line, city, isp)
        city.isNotEmpty() -> city
        isp.isNotEmpty() -> isp
        else -> null
    }
    val secretKindLabel = when (proxy.secretKind()) {
        "ee" -> stringResource(R.string.secret_kind_ee)
        "dd" -> stringResource(R.string.secret_kind_dd)
        else -> stringResource(R.string.secret_kind_plain)
    }
    val updated = formatRelativeUpdatedAt(proxy.update_at)

    val pingHost = proxy.ip?.takeIf { it.isNotBlank() } ?: proxy.host.orEmpty()
    val pingPort = proxy.port?.toIntOrNull() ?: 0
    var pingUi by remember(pingHost, pingPort) { mutableStateOf<PingUi>(PingUi.Measuring) }
    LaunchedEffect(pingHost, pingPort) {
        if (pingHost.isBlank() || pingPort !in 1..65535) {
            pingUi = PingUi.Timeout
            return@LaunchedEffect
        }
        pingUi = PingUi.Measuring
        val ms = tcpPingMs(pingHost, pingPort)
        pingUi = if (ms != null) PingUi.Ready(ms) else PingUi.Timeout
    }
    val pingLabel = when (val p = pingUi) {
        PingUi.Measuring -> stringResource(R.string.ping_measuring)
        PingUi.Timeout -> stringResource(R.string.ping_timeout)
        is PingUi.Ready -> stringResource(R.string.ping_ms, p.ms.toInt().coerceAtMost(Int.MAX_VALUE))
    }

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
                if (metaLine != null) {
                    CupertinoText(
                        text = metaLine,
                        style = CupertinoTheme.typography.caption1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = CupertinoTheme.colorScheme.secondaryLabel,
                    )
                }
                CupertinoText(
                    text = proxy.secret.orEmpty(),
                    style = CupertinoTheme.typography.footnote,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onCopySecret),
                )
                CupertinoText(
                    text = stringResource(
                        R.string.proxy_meta_line,
                        secretKindLabel,
                        pingLabel,
                    ),
                    style = CupertinoTheme.typography.caption2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CupertinoTheme.colorScheme.tertiaryLabel,
                )
                CupertinoText(
                    text = stringResource(R.string.updated_at, updated),
                    style = CupertinoTheme.typography.caption2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CupertinoTheme.colorScheme.tertiaryLabel,
                )
                ProxyConnectButton(
                    text = stringResource(R.string.action_proxy_connect),
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
