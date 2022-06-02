package org.ccci.gto.android.common.util

import java.util.Collections
import java.util.Locale

object LocaleUtils {
    // define a few fixed fallbacks
    internal val FALLBACKS = mutableMapOf<String, String?>(
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
    fun getFallback(locale: Locale) = LOCALE_COMPAT.getFallback(locale)

    @JvmStatic
    fun getFallbacks(locale: Locale): Array<Locale> {
        return LOCALE_COMPAT.getFallbacks(locale)
    }

    @JvmStatic
    fun getFallbacks(vararg locales: Locale): Array<Locale> {
        val outputs = LinkedHashSet<Locale>()

        // generate fallbacks for all provided locales
        for (locale in locales) {
            Collections.addAll(outputs, *getFallbacks(locale))
        }
        return outputs.toTypedArray()
    } // endregion Language fallback methods
}
