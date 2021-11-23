package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.Event

sealed interface CustomEvent<E : CustomEvent<E>> : Event {
    val attributes: MutableMap<String, String?>

    fun attribute(key: String, value: String?): E {
        attributes[key] = value
        @Suppress("UNCHECKED_CAST")
        return this as E
    }
}
