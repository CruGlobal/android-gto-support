package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

@JsonClass(generateAdapter = true)
internal class RawMessage(@Stringify val identifier: Identifier, val data: String) {
    constructor(channel: String, data: String) : this(Identifier(channel), data)
    internal var command = "message"
}
