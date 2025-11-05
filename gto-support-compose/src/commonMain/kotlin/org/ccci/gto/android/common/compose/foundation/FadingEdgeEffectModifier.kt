package org.ccci.gto.android.common.compose.foundation

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalFadingEdgeEffect(scrollState: ScrollState, fade: Dp = 16.dp) =
    verticalFadingEdgeEffect(scrollState = scrollState, fadeTop = fade, fadeBottom = fade)

@Composable
fun Modifier.verticalFadingEdgeEffect(
    scrollState: ScrollState,
    fadeTop: Dp = 16.dp,
    fadeBottom: Dp = 16.dp,
): Modifier {
    val showTopFade by remember { derivedStateOf { fadeTop > 0.dp && scrollState.value > 0 } }
    val showBottomFade by remember { derivedStateOf { fadeBottom > 0.dp && scrollState.value < scrollState.maxValue } }

    return graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            if (showTopFade) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 0f,
                        endY = fadeTop.toPx(),
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }

            if (showBottomFade) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = size.height,
                        endY = (size.height - fadeBottom.toPx()).coerceAtLeast(0f),
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
        }
}
