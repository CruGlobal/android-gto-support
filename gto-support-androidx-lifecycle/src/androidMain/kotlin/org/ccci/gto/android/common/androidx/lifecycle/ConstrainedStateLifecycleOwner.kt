package org.ccci.gto.android.common.androidx.lifecycle

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ConstrainedStateLifecycleOwner private constructor(
    private val parent: Lifecycle,
    maxState: Lifecycle.State,
    isUnsafe: Boolean,
) : LifecycleOwner {
    constructor(
        parent: LifecycleOwner,
        maxState: Lifecycle.State = Lifecycle.State.RESUMED,
    ) : this(parent.lifecycle, maxState, false)

    constructor(
        parent: Lifecycle,
        maxState: Lifecycle.State = Lifecycle.State.RESUMED,
    ) : this(parent, maxState, false)

    companion object {
        @VisibleForTesting
        fun createUnsafe(parent: LifecycleOwner, maxState: Lifecycle.State = Lifecycle.State.RESUMED) =
            ConstrainedStateLifecycleOwner(parent.lifecycle, maxState, isUnsafe = true)

        @VisibleForTesting
        fun createUnsafe(parent: Lifecycle, maxState: Lifecycle.State = Lifecycle.State.RESUMED) =
            ConstrainedStateLifecycleOwner(parent, maxState, isUnsafe = true)
    }

    @SuppressLint("VisibleForTests")
    private val registry = if (isUnsafe) LifecycleRegistry.createUnsafe(this) else LifecycleRegistry(this)
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
