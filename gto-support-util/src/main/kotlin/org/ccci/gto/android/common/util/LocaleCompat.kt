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
    fun getFallback(locale: Locale): Locale?
    fun getFallbacks(locale: Locale): Array<Locale>

    open class Base : LocaleCompat {
        override fun getFallback(locale: Locale) = locale.fallbacksSequence().firstOrNull()

        override fun getFallbacks(locale: Locale): Array<Locale> {
            val locales = LinkedHashSet<Locale>()
            locales.add(locale)
            locales.addAll(locale.fallbacksSequence())
            return locales.toTypedArray()
        }

        protected open fun Locale.fallbacksSequence(): Sequence<Locale> {
            val builder = Locale.Builder().setLocaleSafe(this)
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
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    class Nougat : Base() {
        override fun Locale.fallbacksSequence() =
            generateSequence(ULocale.forLocale(this)) {
                val fixed = LocaleUtils.FALLBACKS[it.toLanguageTag()]
                when {
                    // fixed fallback
                    fixed != null -> ULocale.forLanguageTag(fixed)
                    // remove extensions as the fallback if any are defined
                    extensionKeys.isNotEmpty() -> ULocale.Builder().setLocale(it).clearExtensions().build()
                    // use normal fallback behavior
                    else -> it.fallback.takeUnless { it == ULocale.ROOT }
                }
            }.drop(1).map { it.toLocale() }
    }
}
