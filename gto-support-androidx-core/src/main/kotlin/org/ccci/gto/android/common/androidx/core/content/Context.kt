package org.ccci.gto.android.common.androidx.core.content

import android.content.Context
import androidx.core.os.LocaleListCompat
import org.ccci.gto.android.common.androidx.core.os.toTypedArray
import org.ccci.gto.android.common.util.content.localize

fun Context.localize(locales: LocaleListCompat, includeExisting: Boolean = true) = when {
    locales.isEmpty -> this
    else -> localize(*locales.toTypedArray(), includeExisting = includeExisting)
}
fun Context.localizeIfPossible(locales: LocaleListCompat?) = locales?.let { localize(locales) } ?: this
