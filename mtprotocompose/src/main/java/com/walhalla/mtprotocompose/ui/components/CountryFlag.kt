package com.walhalla.mtprotocompose.ui.components

import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.squareup.picasso.Picasso

private const val FLAG_ASSET = "file:///android_asset/flag/%s.png"

@Composable
fun CountryFlag(code: String?, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.size(64.dp),
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundColor(0xFFE8E8E8.toInt())
            }
        },
        update = { imageView ->
            val flagCode = code?.trim().orEmpty().lowercase()
            if (flagCode.isEmpty()) {
                imageView.setImageDrawable(null)
            } else {
                Picasso.get()
                    .load(String.format(FLAG_ASSET, flagCode))
                    .into(imageView)
            }
        },
    )
}
