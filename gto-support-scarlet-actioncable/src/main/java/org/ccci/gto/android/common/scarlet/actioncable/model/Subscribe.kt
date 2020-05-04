package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

@JsonClass(generateAdapter = true)
class Subscribe(@Stringify val identifier: Identifier) {
    constructor(channel: String) : this(Identifier(channel))
    internal var command = "subscribe"
}
