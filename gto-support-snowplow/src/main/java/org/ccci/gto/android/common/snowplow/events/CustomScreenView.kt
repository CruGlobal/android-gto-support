package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.ScreenView
import com.snowplowanalytics.snowplow.internal.tracker.Tracker

class CustomScreenView : ScreenView, CustomEvent<CustomScreenView> {
    constructor(name: String) : super(name) {
        attributes = mutableMapOf()
    }

    @Deprecated("Snowplow 3.x removes the Event Builder API")
    private constructor(builder: Builder) : super(builder) {
        attributes = builder.attributes.toMutableMap()
    }

    override val attributes: MutableMap<String, String?>

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
        @Deprecated("Snowplow 3.x removes the Event Builder API")
        fun builder() = Builder()
    }

    @Deprecated("Snowplow 3.x removes the Event Builder API")
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
