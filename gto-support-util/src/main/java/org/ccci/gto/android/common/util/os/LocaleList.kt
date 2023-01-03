@file:TargetApi(Build.VERSION_CODES.N)

package org.ccci.gto.android.common.util.os

import android.annotation.TargetApi
import android.os.Build
import android.os.LocaleList
import java.util.Locale

fun LocaleList.listIterator() = object : ListIterator<Locale> {
    private val list = this@listIterator
    private var i = 0

    override fun hasNext() = i < list.size()
    override fun next() = list[i++]
    override fun hasPrevious() = i > 0
    override fun previous() = list[--i]
    override fun nextIndex() = i
    override fun previousIndex() = i - 1
}

internal val LocaleList.locales
    get() = object : Sequence<Locale> {
        override fun iterator() = listIterator()
    }

fun LocaleList.toTypedArray() = locales.toList().toTypedArray()
