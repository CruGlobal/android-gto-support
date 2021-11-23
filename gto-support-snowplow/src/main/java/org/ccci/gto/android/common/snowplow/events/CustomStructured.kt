package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.Structured
import com.snowplowanalytics.snowplow.internal.tracker.Tracker

class CustomStructured : Structured, CustomEvent<CustomStructured> {
    constructor(category: String, action: String) : super(category, action) {
        attributes = mutableMapOf()
    }

    @Deprecated("Snowplow 3.x removes the Event Builder API")
    private constructor(builder: Builder) : super(builder) {
        attributes = builder.attributes
    }

    override val attributes: MutableMap<String, String?>

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

    companion object {
        @Deprecated("Snowplow 3.x removes the Event Builder API")
        fun builder() = Builder()
    }

    @Deprecated("Snowplow 3.x removes the Event Builder API")
    class Builder : Structured.Builder<Builder>(), CustomEventBuilder<Builder> {
        internal val attributes = mutableMapOf<String, String?>()

        override fun attribute(key: String, value: String?): Builder {
            attributes[key] = value
            return this
        }

        override fun self() = this
        override fun build() = CustomStructured(this)
    }
}
