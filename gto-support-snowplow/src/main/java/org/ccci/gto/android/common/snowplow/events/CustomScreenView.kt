package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.tracker.Tracker
import com.snowplowanalytics.snowplow.tracker.events.ScreenView

class CustomScreenView(builder: Builder) : ScreenView(builder) {
    private val attributes = builder.attributes

    override fun beginProcessing(tracker: Tracker) {
        EventSynchronizer.lockFor(this)
        checkNotNull(tracker.subject) { "CustomScreenView requires the Tracker to have a subject to work" }
            .subject.putAll(attributes)
    }

    override fun endProcessing(tracker: Tracker) {
        tracker.subject?.subject?.apply {
            attributes.keys.forEach { remove(it) }
        }
        EventSynchronizer.unlockFor(this)
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder : ScreenView.Builder<Builder>(), CustomEventBuilder<Builder> {
        internal val attributes = mutableMapOf<String, String?>()

        override fun attribute(key: String, value: String?): Builder {
            attributes[key] = value
            return this
        }

        override fun self() = this
        override fun build() = CustomScreenView(this)
    }
}
