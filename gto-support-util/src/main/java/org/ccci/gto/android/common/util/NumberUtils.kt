package org.ccci.gto.android.common.util

import org.ccci.gto.android.common.compat.util.LocaleCompat
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.Locale

fun String.localizedToDoubleOrNull(locale: Locale = LocaleCompat.getDefault(LocaleCompat.Category.FORMAT)): Double? {
    val str = trim()
    return try {
        val pos = ParsePosition(0)
        val num = NumberFormat.getNumberInstance(locale).parse(str, pos)?.toDouble()
        if (pos.index == 0 || pos.index < str.length) null else num
    } catch (_: ParseException) {
        null
    }
}
