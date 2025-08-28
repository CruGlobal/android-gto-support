package org.ccci.gto.android.common.androidx.lifecycle

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ConstrainedStateLifecycleOwner private constructor(
    private val parent: Lifecycle,
    maxState: Lifecycle.State,
    useUnsafeLifecycleRegistry: Boolean,
) : LifecycleOwner {
    constructor(
        parent: LifecycleOwner,
        maxState: Lifecycle.State = Lifecycle.State.RESUMED,
    ) : this(parent.lifecycle, maxState, useUnsafeLifecycleRegistry = false)

    constructor(
        parent: Lifecycle,
        maxState: Lifecycle.State = Lifecycle.State.RESUMED,
    ) : this(parent, maxState, useUnsafeLifecycleRegistry = false)

    companion object {
        @VisibleForTesting
        fun createUnsafe(parent: LifecycleOwner, maxState: Lifecycle.State = Lifecycle.State.RESUMED) =
            ConstrainedStateLifecycleOwner(parent.lifecycle, maxState, useUnsafeLifecycleRegistry = true)

        @VisibleForTesting
        fun createUnsafe(parent: Lifecycle, maxState: Lifecycle.State = Lifecycle.State.RESUMED) =
            ConstrainedStateLifecycleOwner(parent, maxState, useUnsafeLifecycleRegistry = true)
    }

    private val registry = when {
        useUnsafeLifecycleRegistry -> LifecycleRegistry.createUnsafe(this)
        else -> LifecycleRegistry(this)
    }
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
