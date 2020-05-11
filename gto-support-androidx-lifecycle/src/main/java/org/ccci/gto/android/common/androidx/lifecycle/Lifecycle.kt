package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun Lifecycle.onDestroy(block: (owner: LifecycleOwner) -> Unit): LifecycleObserver =
    LambdaLifecycleObserver(onDestroy = block).also { addObserver(it) }

internal class LambdaLifecycleObserver(
    private val onDestroy: ((owner: LifecycleOwner) -> Unit)? = null
) : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy?.invoke(owner)
    }
}
