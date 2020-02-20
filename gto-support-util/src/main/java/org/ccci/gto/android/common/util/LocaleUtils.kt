package org.ccci.gto.android.common.util

import org.ccci.gto.android.common.compat.util.LocaleCompat

@JvmSynthetic
fun String.toLocale() = LocaleCompat.forLanguageTag(this)
