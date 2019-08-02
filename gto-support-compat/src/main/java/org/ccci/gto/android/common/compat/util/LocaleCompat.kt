package org.ccci.gto.android.common.compat.util

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import java.util.Locale

object LocaleCompat {
    private val COMPAT: LocaleCompatMethods = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP -> JellyBeanLocaleCompatMethods()
        else -> LollipopLocaleCompatMethods()
    }

    @JvmStatic
    fun forLanguageTag(tag: String): Locale {
        return COMPAT.forLanguageTag(tag)
    }

    @JvmStatic
    fun toLanguageTag(locale: Locale): String {
        return COMPAT.toLanguageTag(locale)
    }
}

@VisibleForTesting
internal sealed class LocaleCompatMethods {
    abstract fun forLanguageTag(tag: String): Locale
    abstract fun toLanguageTag(locale: Locale): String
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal open class JellyBeanLocaleCompatMethods : LocaleCompatMethods() {
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
internal class LollipopLocaleCompatMethods : JellyBeanLocaleCompatMethods() {
    override fun forLanguageTag(tag: String): Locale = Locale.forLanguageTag(tag)
    override fun toLanguageTag(locale: Locale): String = locale.toLanguageTag()
}
