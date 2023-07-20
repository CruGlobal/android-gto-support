package org.ccci.gto.android.common.util

import android.os.Build
import androidx.annotation.DeprecatedSinceApi
import java.util.IllformedLocaleException
import java.util.Locale

fun Locale.getOptionalDisplayName(inLocale: Locale? = null) = when {
    inLocale != null && getDisplayLanguage(inLocale) == language -> null
    inLocale == null && displayLanguage == language -> null
    inLocale != null -> getDisplayName(inLocale)
    else -> displayName
}

val Locale.fallback get() = LocaleUtils.generateFallbacksSequence(this).firstOrNull()
val Locale.fallbacks get() = LocaleUtils.generateFallbacksSequence(this)

fun Sequence<Locale>.includeFallbacks() = flatMap { sequenceOf(it) + it.fallbacks }

@DeprecatedSinceApi(Build.VERSION_CODES.M)
internal fun Locale.Builder.setLocaleSafe(locale: Locale): Locale.Builder = try {
    setLocale(locale)
} catch (e: IllformedLocaleException) {
    /* HACK: There appears to be a bug on Huawei devices running Android 5.0-5.1.1 using Arabic locales.
             Setting the locale on the Locale Builder throws an IllformedLocaleException for "Invalid
             variant: LNum". To workaround this bug we manually set the locale components on the builder,
             skipping any invalid components
       see: https://gist.github.com/frett/034b8eba09cf815cbcd60f83b3f52eb4
     */
    clear()
    try {
        setLanguage(locale.language)
    } catch (_: IllformedLocaleException) {
    }
    try {
        setScript(locale.script)
    } catch (_: IllformedLocaleException) {
    }
    try {
        setRegion(locale.country)
    } catch (_: IllformedLocaleException) {
    }
    try {
        setVariant(locale.variant)
    } catch (_: IllformedLocaleException) {
    }
    this
}

internal fun Locale.Builder.buildOrNull() = build().takeUnless { it.language.isEmpty() }

@JvmSynthetic
fun String.toLocale() = Locale.forLanguageTag(this)
