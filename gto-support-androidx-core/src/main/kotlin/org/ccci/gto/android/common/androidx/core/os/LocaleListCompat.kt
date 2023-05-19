package org.ccci.gto.android.common.androidx.core.os

import androidx.core.os.LocaleListCompat
import java.util.Locale

fun LocaleListCompat.asIterable(): Iterable<Locale> = LocaleIterable(this)

private class LocaleIterable(private val localeList: LocaleListCompat) : Iterable<Locale> {
    override fun iterator() = LocaleListCompatIterator(localeList)
}

private class LocaleListCompatIterator(private val localeList: LocaleListCompat) : Iterator<Locale> {
    var i = 0
    override fun hasNext(): Boolean = i < localeList.size()
    override fun next(): Locale {
        if (!hasNext()) throw NoSuchElementException()
        return localeList.get(i++)!!
    }
}
