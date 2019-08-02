package org.ccci.gto.android.common.compat.util

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import java.util.Locale

object LocaleCompat {
    private val COMPAT: Compat = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP -> FroyoCompat()
        else -> LollipopCompat()
    }

    @JvmStatic
    fun forLanguageTag(tag: String): Locale {
        return COMPAT.forLanguageTag(tag)
    }

    @JvmStatic
    fun toLanguageTag(locale: Locale): String {
        return COMPAT.toLanguageTag(locale)
    }

    @VisibleForTesting
    internal interface Compat {
        fun forLanguageTag(tag: String): Locale

        fun toLanguageTag(locale: Locale): String
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal open class FroyoCompat : Compat {
        override fun forLanguageTag(tag: String): Locale {
            // XXX: we are ignoring grandfathered tags unless we really need that support
            val subtags = tag.split("-")
            val language = subtags[0]
            val region = if (subtags.size > 1) subtags[1] else ""
            return Locale(language, region)
        }

        override fun toLanguageTag(locale: Locale): String {
            // just perform simple generation
            val sb = StringBuilder(5)

            // append the language
            sb.append(locale.language.toLowerCase(Locale.US))

            // append the region
            val region = locale.country
            if (!region.isNullOrEmpty()) sb.append('-').append(region.toUpperCase(Locale.US))

            // output the language tag
            return sb.toString()
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    internal class LollipopCompat : FroyoCompat() {
        override fun forLanguageTag(tag: String): Locale {
            return Locale.forLanguageTag(tag)
        }

        override fun toLanguageTag(locale: Locale): String {
            return locale.toLanguageTag()
        }
    }
}
