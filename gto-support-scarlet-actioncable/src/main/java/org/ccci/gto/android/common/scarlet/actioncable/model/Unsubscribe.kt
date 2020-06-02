package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

private const val COMMAND = "unsubscribe"

@JsonClass(generateAdapter = true)
class Unsubscribe internal constructor(@Stringify val identifier: Identifier, internal val command: String) {
    constructor(identifier: Identifier) : this(identifier, COMMAND)
    constructor(channel: String) : this(Identifier(channel))

    init {
        require(command == COMMAND) { "Invalid command: $command" }
    }
}
