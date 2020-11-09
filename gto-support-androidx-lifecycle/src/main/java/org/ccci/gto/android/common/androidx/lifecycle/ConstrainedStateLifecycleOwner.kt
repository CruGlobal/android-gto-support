package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ConstrainedStateLifecycleOwner(private val parent: Lifecycle) : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    private val parentEventObserver = LifecycleEventObserver { _, _ -> reconcileState() }
    init {
        parent.addObserver(parentEventObserver)
        lifecycle.onDestroy { parent.removeObserver(parentEventObserver) }
    }

    var maxState = Lifecycle.State.RESUMED
        set(value) {
            field = value
            reconcileState()
        }

    override fun getLifecycle(): Lifecycle = registry

    private fun reconcileState() {
        registry.currentState = minOf(parent.currentState, maxState)
    }
}
