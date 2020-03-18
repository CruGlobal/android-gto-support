package org.ccci.gto.android.common.compat.util

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import java.util.Locale

private val REGEX_REGION = "([a-z]{2}|[0-9]{3})".toRegex(RegexOption.IGNORE_CASE)

object LocaleCompat {
    private val COMPAT: LocaleCompatMethods = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> NougatLocaleCompatMethods()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> LollipopLocaleCompatMethods()
        else -> BaseLocaleCompatMethods()
    }

    @JvmStatic
    fun getDefault(category: Category) = COMPAT.getDefault(category)

    @JvmStatic
    fun forLanguageTag(tag: String) = COMPAT.forLanguageTag(tag)

    @JvmStatic
    fun toLanguageTag(locale: Locale) = COMPAT.toLanguageTag(locale)

    enum class Category { DISPLAY, FORMAT }
}

@VisibleForTesting
internal sealed class LocaleCompatMethods {
    abstract fun getDefault(category: LocaleCompat.Category): Locale
    abstract fun forLanguageTag(tag: String): Locale
    abstract fun toLanguageTag(locale: Locale): String
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal open class BaseLocaleCompatMethods : LocaleCompatMethods() {
    override fun getDefault(category: LocaleCompat.Category): Locale = Locale.getDefault()

    override fun forLanguageTag(tag: String): Locale {
        // XXX: we are ignoring grandfathered tags unless we really need that support
        val subtags = tag.split("-")
        val language = subtags[0]
        val region = subtags
            // skip the language
            .drop(1)
            // discard any extensions
            .takeWhile { it.length != 1 }
            // first component that is a REGION
            .firstOrNull { REGEX_REGION.matches(it) } ?: ""
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
internal open class LollipopLocaleCompatMethods : BaseLocaleCompatMethods() {
    override fun forLanguageTag(tag: String): Locale = Locale.forLanguageTag(tag)
    override fun toLanguageTag(locale: Locale): String = locale.toLanguageTag()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@TargetApi(Build.VERSION_CODES.N)
internal class NougatLocaleCompatMethods : LollipopLocaleCompatMethods() {
    override fun getDefault(category: LocaleCompat.Category): Locale = Locale.getDefault(
        when (category) {
            LocaleCompat.Category.DISPLAY -> Locale.Category.DISPLAY
            LocaleCompat.Category.FORMAT -> Locale.Category.FORMAT
        }
    )
}
