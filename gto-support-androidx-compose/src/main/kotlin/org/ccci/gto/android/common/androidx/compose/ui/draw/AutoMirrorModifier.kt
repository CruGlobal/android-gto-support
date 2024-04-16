package org.ccci.gto.android.common.androidx.compose.ui.draw

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Deprecated("Since v4.2.1, Material Icons now provide AutoMirrored versions, so this should not be necessary.")
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.autoMirror() = composed {
    when (LocalLayoutDirection.current) {
        LayoutDirection.Rtl -> Modifier.scale(-1f, 1f)
        else -> Modifier
    }
}
