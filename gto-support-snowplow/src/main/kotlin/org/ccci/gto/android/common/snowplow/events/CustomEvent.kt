package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.Event

@Deprecated("Since v3.14.0, We no longer use snowplow for any analytics")
sealed interface CustomEvent<E : CustomEvent<E>> : Event {
    val attributes: MutableMap<String, String?>

    fun attribute(key: String, value: String?): E {
        attributes[key] = value
        @Suppress("UNCHECKED_CAST")
        return this as E
    }
}
