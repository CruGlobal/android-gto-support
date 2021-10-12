package org.ccci.gto.android.common.scarlet

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Lifecycle.State
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import java.util.WeakHashMap

class ReferenceLifecycle private constructor(private val registry: LifecycleRegistry) : Lifecycle by registry {
    constructor() : this(LifecycleRegistry())

    private var references = WeakHashMap<Any, Unit>()

    init {
        updateState()
    }

    @Synchronized
    fun acquire(obj: Any = Any()): Any {
        references[obj] = Unit
        updateState()
        return obj
    }

    @Synchronized
    fun release(obj: Any) {
        references.remove(obj)
        updateState()
    }

    private fun updateState() {
        when {
            references.isNotEmpty() -> registry.onNext(State.Started)
            else -> registry.onNext(State.Stopped.WithReason(ShutdownReason(1000, "No active references")))
        }
    }
}
