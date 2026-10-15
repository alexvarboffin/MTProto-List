package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoButton
import com.slapps.cupertino.CupertinoButtonDefaults
import com.slapps.cupertino.ExperimentalCupertinoApi

/** Flag in Cupertino gray button — tap opens info. */
@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun ProxyFlagInfoButton(
    code: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CupertinoButton(
        onClick = onClick,
        modifier = modifier,
        colors = CupertinoButtonDefaults.grayButtonColors(),
        contentPadding = PaddingValues(6.dp),
    ) {
        CountryFlag(code = code, modifier = Modifier.size(56.dp))
    }
}