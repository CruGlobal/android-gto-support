package org.ccci.gto.android.common.androidx.activity.compose

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun RequestedOrientation(orientation: Int) {
    val activity = LocalActivity.current
    DisposableEffect(activity, orientation) {
        if (activity == null) return@DisposableEffect onDispose { }
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}
