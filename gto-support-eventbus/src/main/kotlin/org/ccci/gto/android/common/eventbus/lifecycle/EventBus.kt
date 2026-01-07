package org.ccci.gto.android.common.eventbus.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.ccci.gto.android.common.androidx.lifecycle.onStart
import org.ccci.gto.android.common.androidx.lifecycle.onStop
import org.greenrobot.eventbus.EventBus

fun EventBus.register(
    lifecycleOwner: LifecycleOwner,
    subscriber: Any,
    atLeast: Lifecycle.State = Lifecycle.State.STARTED,
) {
    when (atLeast) {
        Lifecycle.State.STARTED -> {
            lifecycleOwner.lifecycle.onStart { register(subscriber) }
            lifecycleOwner.lifecycle.onStop { unregister(subscriber) }
        }

        else -> TODO("This state is not supported yet")
    }
}
