package org.ccci.gto.android.common.snowplow.events

import androidx.annotation.VisibleForTesting
import com.snowplowanalytics.snowplow.tracker.events.Event
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

internal object EventSynchronizer {
    @VisibleForTesting
    internal val semaphore = Semaphore(1, true)
    private val currentEvent = AtomicReference<Event?>()

    @VisibleForTesting
    internal var lockTimeout = 2000L

    fun lockFor(event: Event) {
        while (true) {
            val curr = currentEvent.get()
            if (semaphore.tryAcquire(lockTimeout, TimeUnit.MILLISECONDS)) {
                if (currentEvent.compareAndSet(null, event)) break
                semaphore.release()
            } else if (curr != null && currentEvent.compareAndSet(curr, event)) {
                break
            }
        }
    }

    fun unlockFor(event: Event) {
        if (currentEvent.compareAndSet(event, null)) semaphore.release()
    }
}
