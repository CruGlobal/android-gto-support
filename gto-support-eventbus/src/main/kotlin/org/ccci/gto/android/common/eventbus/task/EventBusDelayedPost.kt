package org.ccci.gto.android.common.eventbus.task

import org.greenrobot.eventbus.EventBus

class EventBusDelayedPost(private val eventBus: EventBus, private vararg val events: Any?) : Runnable {
    override fun run() {
        events.forEach { if (it != null) eventBus.post(it) }
    }
}
