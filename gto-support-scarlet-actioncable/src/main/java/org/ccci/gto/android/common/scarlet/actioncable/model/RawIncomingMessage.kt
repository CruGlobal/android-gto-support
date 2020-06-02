package org.ccci.gto.android.common.scarlet.actioncable.model

import com.squareup.moshi.JsonClass
import org.ccci.gto.android.common.moshi.adapter.Stringify

@JsonClass(generateAdapter = true)
internal class RawIncomingMessage(@Stringify val identifier: Identifier, val message: Any) {
    internal var command = "message"
}
