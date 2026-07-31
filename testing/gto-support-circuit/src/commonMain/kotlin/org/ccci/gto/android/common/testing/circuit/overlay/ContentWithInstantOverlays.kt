package org.ccci.gto.android.common.testing.circuit.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.overlay.LocalOverlayState
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayState
import com.slack.circuit.overlay.rememberOverlayHost

/**
 * A testing variant of [com.slack.circuit.overlay.ContentWithOverlays] that shows and hides overlays instantly,
 * without any animations. This provides [LocalOverlayHost] and [LocalOverlayState] to [content] and renders the current
 * overlay (if any) on top of [content].
 *
 * @param modifier The modifier to apply to the container holding [content] and any displayed overlay.
 * @param overlayHost The [OverlayHost] used to track and display overlays.
 * @param content The content to display beneath any overlays.
 */
@Composable
fun ContentWithInstantOverlays(
    modifier: Modifier = Modifier,
    overlayHost: OverlayHost = rememberOverlayHost(),
    content: @Composable () -> Unit,
) {
    val overlayHostData by rememberUpdatedState(overlayHost.currentOverlayData)
    val overlayState by remember {
        derivedStateOf { overlayHostData?.let { OverlayState.SHOWING } ?: OverlayState.HIDDEN }
    }
    CompositionLocalProvider(
        LocalOverlayHost provides overlayHost,
        LocalOverlayState provides overlayState,
    ) {
        Box(modifier) {
            content()
            overlayHostData?.let { data ->
                data.overlay.Content(data::finish)
            }
        }
    }
}
