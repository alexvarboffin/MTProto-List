package com.walhalla.mtprotocompose.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiOverlay(
    trigger: Int,
    modifier: Modifier = Modifier,
) {
    if (trigger <= 0) return
    val konfettiView = remember { mutableListOf<KonfettiView>() }
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            KonfettiView(context).also { konfettiView.clear(); konfettiView.add(it) }
        },
    )
    LaunchedEffect(trigger) {
        konfettiView.firstOrNull()?.start(
            PartyFactory(Emitter(100L, TimeUnit.MILLISECONDS).max(100))
                .spread(360)
                .shapes(listOf(Shape.Square, Shape.Circle))
                .colors(listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(0f, 30f)
                .position(Position.Relative(0.5, 0.3))
                .build(),
        )
    }
}
