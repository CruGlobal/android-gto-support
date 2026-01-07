package org.ccci.gto.android.common.scarlet

import com.tinder.scarlet.Message
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString

private val UTF8_BOM = "EFBBBF".decodeHex()
val Message.stringValue get() = when (this) {
    is Message.Text -> value

    is Message.Bytes -> {
        val byteString = value.toByteString(0, value.size)

        // strip off the utf-8 BOM
        if (byteString.startsWith(UTF8_BOM)) {
            byteString.substring(UTF8_BOM.size).utf8()
        } else {
            byteString.utf8()
        }
    }
}
