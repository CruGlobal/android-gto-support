package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.tracker.events.AbstractEvent

interface CustomEventBuilder<B : AbstractEvent.Builder<B>> {
    fun attribute(key: String, value: String?): B
}
