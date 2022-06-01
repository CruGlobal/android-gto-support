package org.ccci.gto.android.common.util

import java.util.Locale

fun Locale.getOptionalDisplayName(inLocale: Locale? = null) = when {
    inLocale != null && getDisplayLanguage(inLocale) == language -> null
    inLocale == null && displayLanguage == language -> null
    inLocale != null -> getDisplayName(inLocale)
    else -> displayName
}

@JvmSynthetic
fun String.toLocale() = Locale.forLanguageTag(this)
