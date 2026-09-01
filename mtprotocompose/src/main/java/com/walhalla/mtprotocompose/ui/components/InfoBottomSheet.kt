package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.walhalla.mtprotocompose.BuildConfig
import com.walhalla.mtprotocompose.R
import com.walhalla.mtprotocompose.util.openMaps
import com.walhalla.mtprotolist.entity.MtprotoProxy
import com.walhalla.mtprotolist.webproxy.ProxyInfo

sealed class InfoTarget {
    data class Mtproto(val proxy: MtprotoProxy) : InfoTarget()
    data class Web(val proxy: ProxyInfo) : InfoTarget()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    target: InfoTarget,
    onDismiss: () -> Unit,
    onToggleEnabled: (() -> Unit)? = null,
    onUpdateGeo: (() -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            when (target) {
                is InfoTarget.Mtproto -> {
                    val p = target.proxy
                    InfoLine(stringResource(R.string.country, p.country.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.country_code, p.code.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.region, p.region.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.region_name, p.regionName.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.city, p.city.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.zip, p.zip.orEmpty().ifBlank { "-" }))
                    Text(
                        text = stringResource(R.string.lat_lon, p.lat, p.lon),
                        style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable { openMaps(context, p.lat, p.lon) },
                    )
                    InfoLine(stringResource(R.string.timezone, p.timezone.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.isp, p.isp.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.org, p.org.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.as_info, p.`as`.orEmpty().ifBlank { "-" }))
                }
                is InfoTarget.Web -> {
                    val p = target.proxy
                    InfoLine(stringResource(R.string.country, p.country.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.country_code, p.code.ifBlank { "-" }))
                    InfoLine(stringResource(R.string.region, p.region.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.region_name, p.regionName.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.city, p.city.orEmpty().ifBlank { "-" }))
                    InfoLine(stringResource(R.string.zip, p.zip.orEmpty().ifBlank { "-" }))
                    Text(
                        text = stringResource(R.string.lat_lon, p.lat, p.lon),
                        style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = onToggleEnabled, modifier = Modifier.weight(1f)) {
                        val enabled = when (target) {
                            is InfoTarget.Mtproto -> target.proxy.enabled != false
                            is InfoTarget.Web -> target.proxy.enabled
                        }
                        Text(if (enabled) "DISABLE" else "ENABLE")
                    }
                    Button(onClick = onUpdateGeo, modifier = Modifier.weight(1f)) {
                        Text("UPDATE GEO")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium)
}
