package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

data class Identifier(val channel: String, val attrs: Map<String, Any?> = emptyMap())

internal object IdentifierJsonAdapter {
    @ToJson
    fun toJson(identifier: Identifier?) = identifier?.let { it.attrs + ("channel" to it.channel) }
    @FromJson
    fun fromJson(json: Map<String, Any?>?) =
        json?.get("channel")?.takeIf { it is String }?.let { Identifier(it as String, json - "channel") }
}
