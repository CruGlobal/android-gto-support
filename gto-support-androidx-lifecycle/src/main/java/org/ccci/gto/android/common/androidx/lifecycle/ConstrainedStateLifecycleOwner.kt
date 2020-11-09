package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ConstrainedStateLifecycleOwner(private val parent: Lifecycle) : LifecycleOwner {
    private val registry = LifecycleRegistry(this)

    var maxState = Lifecycle.State.RESUMED
        set(value) {
            field = value
            reconcileState()
        }

    init {
        val observer = LifecycleEventObserver { _, _ -> reconcileState() }
        parent.addObserver(observer)
        lifecycle.onDestroy { parent.removeObserver(observer) }
    }

    override fun getLifecycle(): Lifecycle = registry

    private fun reconcileState() {
        registry.currentState = minOf(parent.currentState, maxState)
    }
}
