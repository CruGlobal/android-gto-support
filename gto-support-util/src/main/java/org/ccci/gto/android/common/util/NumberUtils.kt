package org.ccci.gto.android.common.util

import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat
import org.ccci.gto.android.common.compat.util.LocaleCompat.Category

fun String.localizedToDoubleOrNull(locale: Locale = LocaleCompat.getDefault(Category.FORMAT)): Double? {
    val str = trim()
    return try {
        val pos = ParsePosition(0)
        val num = NumberFormat.getNumberInstance(locale).parse(str, pos)?.toDouble()
        if (pos.index == 0 || pos.index < str.length) null else num
    } catch (_: ParseException) {
        null
    }
}

fun Number.format(
    locale: Locale = LocaleCompat.getDefault(Category.FORMAT),
    minimumIntegerDigits: Int = 1,
    maximumIntegerDigits: Int = 40,
    minimumFractionDigits: Int = 0,
    maximumFractionDigits: Int = 3
): String = NumberFormat.getNumberInstance(locale)
    .also {
        it.minimumIntegerDigits = minimumIntegerDigits
        it.maximumIntegerDigits = maximumIntegerDigits
        it.minimumFractionDigits = minimumFractionDigits
        it.maximumFractionDigits = maximumFractionDigits
    }
    .format(this)
