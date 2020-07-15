package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun Lifecycle.onStart(block: (owner: LifecycleOwner) -> Unit): LifecycleObserver =
    LambdaLifecycleObserver(onStart = block).also { addObserver(it) }

fun Lifecycle.onStop(block: (owner: LifecycleOwner) -> Unit): LifecycleObserver =
    LambdaLifecycleObserver(onStop = block).also { addObserver(it) }

fun Lifecycle.onDestroy(block: (owner: LifecycleOwner) -> Unit): LifecycleObserver =
    LambdaLifecycleObserver(onDestroy = block).also { addObserver(it) }

internal class LambdaLifecycleObserver(
    private val onStart: ((owner: LifecycleOwner) -> Unit)? = null,
    private val onStop: ((owner: LifecycleOwner) -> Unit)? = null,
    private val onDestroy: ((owner: LifecycleOwner) -> Unit)? = null
) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        onStart?.invoke(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        onStop?.invoke(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy?.invoke(owner)
    }
}
