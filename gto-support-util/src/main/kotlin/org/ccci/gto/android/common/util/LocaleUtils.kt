package org.ccci.gto.android.common.util

import java.util.Locale

object LocaleUtils {
    // define a few fixed fallbacks
    internal val FALLBACKS = mutableMapOf(
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
        require(FALLBACKS[locale] == null) { "$locale already has a fallback language defined" }
        FALLBACKS[locale] = fallback
    }

    @JvmStatic
    fun getFallback(locale: Locale) = LOCALE_COMPAT.generateFallbacksSequence(locale).firstOrNull()

    @JvmStatic
    fun getFallbacks(locale: Locale) =
        (sequenceOf(locale) + LOCALE_COMPAT.generateFallbacksSequence(locale)).distinct().toList().toTypedArray()

    @JvmStatic
    fun getFallbacks(vararg locales: Locale) = locales.asSequence()
        .flatMap { sequenceOf(it) + LOCALE_COMPAT.generateFallbacksSequence(it) }
        .distinct()
        .toList().toTypedArray()
    // endregion Language fallback methods
}
