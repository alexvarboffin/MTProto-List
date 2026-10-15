package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoAlertDialog
import com.slapps.cupertino.CupertinoButton
import com.slapps.cupertino.CupertinoButtonDefaults
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.ExperimentalCupertinoApi
import com.slapps.cupertino.default
import com.slapps.cupertino.theme.CupertinoTheme
import com.walhalla.mtprotocompose.BuildConfig
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.util.formatRelativeUpdatedAt
import com.walhalla.mtprotocompose.util.formatUtcOffsetSeconds
import com.walhalla.mtprotocompose.util.openMaps
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo

sealed class InfoTarget {
    data class Mtproto(val proxy: MtprotoProxy) : InfoTarget()
    data class Web(val proxy: ProxyInfo) : InfoTarget()
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun InfoBottomSheet(
    target: InfoTarget,
    onDismiss: () -> Unit,
    onToggleEnabled: (() -> Unit)? = null,
    onUpdateGeo: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val maxBodyHeight = (LocalConfiguration.current.screenHeightDp * 0.55f).dp

    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = { CupertinoText(stringResource(R.string.app_name)) },
        message = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxBodyHeight)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                when (target) {
                    is InfoTarget.Mtproto -> {
                        val p = target.proxy
                        val yes = stringResource(R.string.yes)
                        val no = stringResource(R.string.no)
                        val secretKindLabel = when (p.secretKind()) {
                            "ee" -> stringResource(R.string.secret_kind_ee)
                            "dd" -> stringResource(R.string.secret_kind_dd)
                            else -> stringResource(R.string.secret_kind_plain)
                        }
                        InfoLine(stringResource(R.string.proxy_ip, p.ip.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.country, p.country.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.country_code, p.code.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.continent, p.continent.orEmpty().ifBlank { "-" }))
                        InfoLine(
                            stringResource(
                                R.string.continent_code,
                                p.continentCode.orEmpty().ifBlank { "-" },
                            ),
                        )
                        InfoLine(stringResource(R.string.region, p.region.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.region_name, p.regionName.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.city, p.city.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.district, p.district.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.zip, p.zip.orEmpty().ifBlank { "-" }))
                        CupertinoText(
                            text = stringResource(R.string.lat_lon, p.lat, p.lon),
                            style = CupertinoTheme.typography.body.copy(
                                textDecoration = TextDecoration.Underline,
                            ),
                            modifier = Modifier.clickable { openMaps(context, p.lat, p.lon) },
                        )
                        InfoLine(stringResource(R.string.timezone, p.timezone.orEmpty().ifBlank { "-" }))
                        InfoLine(
                            stringResource(
                                R.string.timezone_offset,
                                formatUtcOffsetSeconds(p.offset),
                            ),
                        )
                        InfoLine(stringResource(R.string.currency, p.currency.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.isp, p.isp.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.org, p.org.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.as_info, p.`as`.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.asname, p.asname.orEmpty().ifBlank { "-" }))
                        InfoLine(
                            stringResource(
                                R.string.flag_mobile,
                                p.mobile?.let { if (it) yes else no } ?: "-",
                            ),
                        )
                        InfoLine(
                            stringResource(
                                R.string.flag_proxy,
                                p.isProxy?.let { if (it) yes else no } ?: "-",
                            ),
                        )
                        InfoLine(
                            stringResource(
                                R.string.flag_hosting,
                                p.hosting?.let { if (it) yes else no } ?: "-",
                            ),
                        )
                        InfoLine(stringResource(R.string.secret_kind, secretKindLabel))
                        InfoLine(
                            stringResource(
                                R.string.updated_at,
                                formatRelativeUpdatedAt(p.update_at),
                            ),
                        )
                    }
                    is InfoTarget.Web -> {
                        val p = target.proxy
                        InfoLine(stringResource(R.string.proxy_ip, p.ip.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.country, p.country.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.country_code, p.code.ifBlank { "-" }))
                        InfoLine(stringResource(R.string.region, p.region.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.region_name, p.regionName.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.city, p.city.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.zip, p.zip.orEmpty().ifBlank { "-" }))
                        CupertinoText(
                            text = stringResource(R.string.lat_lon, p.lat, p.lon),
                            style = CupertinoTheme.typography.body.copy(
                                textDecoration = TextDecoration.Underline,
                            ),
                            modifier = Modifier.clickable { openMaps(context, p.lat, p.lon) },
                        )
                        InfoLine(stringResource(R.string.timezone, p.timezone.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.isp, p.isp.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.org, p.org.orEmpty().ifBlank { "-" }))
                        InfoLine(stringResource(R.string.as_info, p.`as`.orEmpty().ifBlank { "-" }))
                    }
                }
                if (BuildConfig.DEBUG && onToggleEnabled != null && onUpdateGeo != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CupertinoButton(
                            onClick = onToggleEnabled,
                            modifier = Modifier.weight(1f),
                            colors = CupertinoButtonDefaults.grayButtonColors(),
                        ) {
                            val enabled = when (target) {
                                is InfoTarget.Mtproto -> target.proxy.enabled != false
                                is InfoTarget.Web -> target.proxy.enabled
                            }
                            CupertinoText(
                                if (enabled) {
                                    stringResource(R.string.action_disable)
                                } else {
                                    stringResource(R.string.action_enable)
                                },
                            )
                        }
                        CupertinoButton(
                            onClick = onUpdateGeo,
                            modifier = Modifier.weight(1f),
                            colors = CupertinoButtonDefaults.filledButtonColors(),
                        ) {
                            CupertinoText(stringResource(R.string.action_update_geo))
                        }
                    }
                }
            }
        },
        buttons = {
            default(onClick = onDismiss) {
                CupertinoText(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun InfoLine(text: String) {
    CupertinoText(text = text, style = CupertinoTheme.typography.body)
}
