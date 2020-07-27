package org.ccci.gto.android.common.util.content

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import java.util.Locale
import org.ccci.gto.android.common.util.os.locales

fun Context.localize(vararg locales: Locale, includeExisting: Boolean = true): Context = when {
    locales.isEmpty() -> this
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> createConfigurationContext(Configuration().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setLocales(
                LocaleList(
                    *LinkedHashSet<Locale>().apply {
                        addAll(locales)
                        if (includeExisting) addAll(resources.configuration.locales.locales)
                    }.toTypedArray()
                )
            )
            else -> setLocale(locales.first())
        }
    })
    else -> this
}

fun Context.getString(locale: Locale?, @StringRes resId: Int, vararg formatArgs: Any?) =
    (locale?.let { localize(locale) } ?: this).getString(resId, *formatArgs)
