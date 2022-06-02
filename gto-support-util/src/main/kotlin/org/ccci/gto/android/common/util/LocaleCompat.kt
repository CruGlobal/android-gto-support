package org.ccci.gto.android.common.util

import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale

internal val LOCALE_COMPAT: LocaleCompat by lazy {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> LocaleCompat.Base()
        else -> LocaleCompat.Nougat()
    }
}

internal sealed interface LocaleCompat {
    fun generateFallbacksSequence(locale: Locale): Sequence<Locale>

    open class Base : LocaleCompat {
        override fun generateFallbacksSequence(locale: Locale): Sequence<Locale> {
            val builder = Locale.Builder().setLocaleSafe(locale)
            return generateSequence {
                val currentLocale = builder.buildOrNull() ?: return@generateSequence null
                val directFallback = LocaleUtils.FALLBACKS[currentLocale.toLanguageTag()]
                when {
                    directFallback != null -> {
                        val fallbackLocale = Locale.forLanguageTag(directFallback)
                        builder.setLocaleSafe(fallbackLocale)
                        fallbackLocale
                    }
                    currentLocale.extensionKeys.isNotEmpty() -> builder.clearExtensions().build()
                    currentLocale.variant.isNotEmpty() -> builder.setVariant(null).build()
                    currentLocale.country.isNotEmpty() -> builder.setRegion(null).build()
                    currentLocale.script.isNotEmpty() -> builder.setScript(null).build()
                    else -> {
                        builder.clear()
                        null
                    }
                }
            }.distinct()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    class Nougat : Base() {
        override fun generateFallbacksSequence(locale: Locale) =
            generateSequence(ULocale.forLocale(locale)) {
                val fixed = LocaleUtils.FALLBACKS[it.toLanguageTag()]
                when {
                    // fixed fallback
                    fixed != null -> ULocale.forLanguageTag(fixed)
                    // remove extensions as the fallback if any are defined
                    locale.extensionKeys.isNotEmpty() -> ULocale.Builder().setLocale(it).clearExtensions().build()
                    // use normal fallback behavior
                    else -> it.fallback.takeUnless { it == ULocale.ROOT }
                }
            }.drop(1).map { it.toLocale() }.distinct()
    }
}
