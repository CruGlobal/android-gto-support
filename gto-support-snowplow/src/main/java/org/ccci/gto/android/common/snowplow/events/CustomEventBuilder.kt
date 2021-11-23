package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.AbstractEvent

@Deprecated("Snowplow 3.x removes the Event Builder API")
sealed interface CustomEventBuilder<B : AbstractEvent.Builder<B>> {
    fun attribute(key: String, value: String?): B
}
