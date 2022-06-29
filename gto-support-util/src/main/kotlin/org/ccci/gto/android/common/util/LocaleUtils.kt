package org.ccci.gto.android.common.util

import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale

object LocaleUtils {
    private val COMPAT = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> Compat.Base()
        else -> Compat.Nougat()
    }

    private val FIXED_FALLBACKS = mutableMapOf(
        // macro language fallbacks https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
        // Malagasy macro language
        "bhr" to "mg",
        "bjq" to "mg",
        "bmm" to "mg",
        "bzc" to "mg",
        "msh" to "mg",
        "plt" to "mg",
        "skg" to "mg",
        "tdx" to "mg",
        "tkg" to "mg",
        "txy" to "mg",
        "xmv" to "mg",
        "xmw" to "mg",

        // Malay macrolanguage
        "mfa" to "ms",
        "pse" to "ms",
        "zlm" to "ms"
    )

    // region Language fallback methods
    @JvmStatic
    fun addFallback(locale: String, fallback: String) {
        require(FIXED_FALLBACKS[locale] == null) { "$locale already has a fallback language defined" }
        FIXED_FALLBACKS[locale] = fallback
    }

    internal fun generateFallbacksSequence(locale: Locale) = COMPAT.generateFallbacksSequence(locale)
    // endregion Language fallback methods

    private sealed interface Compat {
        fun generateFallbacksSequence(locale: Locale): Sequence<Locale>

        open class Base : Compat {
            override fun generateFallbacksSequence(locale: Locale): Sequence<Locale> {
                val builder = Locale.Builder().setLocaleSafe(locale)
                return generateSequence {
                    val currentLocale = builder.buildOrNull() ?: return@generateSequence null
                    val directFallback = FIXED_FALLBACKS[currentLocale.toLanguageTag()]
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
                    val fixed = FIXED_FALLBACKS[it.toLanguageTag()]
                    when {
                        // fixed fallback
                        fixed != null -> ULocale.forLanguageTag(fixed)
                        // remove extensions as the fallback if any are defined
                        it.extensionKeys.isNotEmpty() -> ULocale.Builder().setLocale(it).clearExtensions().build()
                        // use normal fallback behavior
                        else -> it.fallback.takeUnless { it == ULocale.ROOT }
                    }
                }.drop(1).map { it.toLocale() }.distinct()
        }
    }
}
