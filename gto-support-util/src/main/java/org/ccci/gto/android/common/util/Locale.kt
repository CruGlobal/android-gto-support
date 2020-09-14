package org.ccci.gto.android.common.util

import java.util.Locale
import org.ccci.gto.android.common.compat.util.LocaleCompat

fun Locale.getOptionalDisplayName(inLocale: Locale? = null) = when {
    inLocale != null && getDisplayLanguage(inLocale) == language -> null
    inLocale == null && displayLanguage == language -> null
    inLocale != null -> getDisplayName(inLocale)
    else -> displayName
}

@JvmSynthetic
fun String.toLocale() = LocaleCompat.forLanguageTag(this)
