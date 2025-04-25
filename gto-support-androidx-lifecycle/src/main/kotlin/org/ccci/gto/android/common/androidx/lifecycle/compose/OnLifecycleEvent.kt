package org.ccci.gto.android.common.androidx.lifecycle.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun OnLifecycleEvent(vararg keys: Any?, onEvent: (event: Lifecycle.Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val eventHandler by rememberUpdatedState(onEvent)

    DisposableEffect(lifecycleOwner, *keys) {
        val observer = LifecycleEventObserver { _, event -> eventHandler(event) }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
fun OnResume(vararg keys: Any?, onResume: () -> Unit) =
    OnLifecycleEvent(*keys) { if (it == Lifecycle.Event.ON_RESUME) onResume() }
