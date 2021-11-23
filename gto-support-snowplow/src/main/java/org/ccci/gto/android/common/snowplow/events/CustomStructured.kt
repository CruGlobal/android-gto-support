package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.Structured
import com.snowplowanalytics.snowplow.internal.tracker.Tracker

class CustomStructured(category: String, action: String) : Structured(category, action), CustomEvent<CustomStructured> {
    override val attributes = mutableMapOf<String, String?>()

    override fun beginProcessing(tracker: Tracker) {
        EventSynchronizer.lockFor(this)
        checkNotNull(tracker.subject) { "CustomStructured requires the Tracker to have a subject to work" }
            .subject.putAll(attributes)
    }

    override fun endProcessing(tracker: Tracker) {
        tracker.subject?.subject?.apply {
            attributes.keys.forEach { remove(it) }
        }
        EventSynchronizer.unlockFor(this)
    }
}
