package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

private const val TYPE = "confirm_subscription"

@JsonClass(generateAdapter = true)
data class ConfirmSubscription internal constructor(@Stringify val identifier: Identifier, internal val type: String) {
    constructor(identifier: Identifier) : this(identifier, TYPE)
    internal constructor(channel: String) : this(Identifier(channel))

    init {
        require(type == TYPE) { "Invalid type: $type" }
    }
}
