package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

@JsonClass(generateAdapter = true)
internal class IncomingMessage(@Stringify val identifier: Identifier, val message: String) {
    internal var command = "message"
}
