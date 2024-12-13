package org.ccci.gto.android.common.androidx.compose.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun FixStatusBarsForegroundColor(backgroundColor: Color) {
    val context = LocalContext.current
    DisposableEffect(context, backgroundColor) {
        val controller = (context as? Activity)?.window?.let { WindowInsetsControllerCompat(it, it.decorView) }
        val originalValue = controller?.isAppearanceLightStatusBars ?: false
        controller?.isAppearanceLightStatusBars = backgroundColor.luminance() > 0.5f

        onDispose {
            controller?.isAppearanceLightStatusBars = originalValue
        }
    }
}
