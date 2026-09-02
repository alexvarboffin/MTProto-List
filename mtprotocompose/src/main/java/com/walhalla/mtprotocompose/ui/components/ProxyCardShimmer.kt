package com.walhalla.mtprotocompose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun Modifier.proxyCardShimmer(enabled: Boolean): Modifier {
    if (!enabled) return this
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    return this.shimmer(shimmer)
}
