package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ConstrainedStateLifecycleOwner(
    private val parent: Lifecycle,
    maxState: Lifecycle.State = Lifecycle.State.RESUMED
) : LifecycleOwner {
    constructor(
        parent: LifecycleOwner,
        maxState: Lifecycle.State = Lifecycle.State.RESUMED
    ) : this(parent.lifecycle, maxState)

    private val registry = LifecycleRegistry(this)
    override val lifecycle = registry

    var maxState = maxState
        set(value) {
            field = value
            reconcileState()
        }

    init {
        val observer = LifecycleEventObserver { _, _ -> reconcileState() }
        parent.addObserver(observer)
        lifecycle.onDestroy { parent.removeObserver(observer) }
    }

    private fun reconcileState() {
        registry.currentState = minOf(parent.currentState, maxState)
    }
}
