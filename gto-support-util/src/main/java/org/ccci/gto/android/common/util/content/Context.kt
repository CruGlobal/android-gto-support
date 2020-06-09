package org.ccci.gto.android.common.util.content

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import org.ccci.gto.android.common.util.os.locales
import java.util.Locale

@OptIn(ExperimentalStdlibApi::class)
fun Context.localize(vararg locales: Locale, includeExisting: Boolean = true): Context = when {
    locales.isEmpty() -> this
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> createConfigurationContext(Configuration().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setLocales(
                LocaleList(
                    *buildList<Locale> {
                        addAll(locales)
                        if (includeExisting) addAll(resources.configuration.locales.locales)
                    }.distinct().toTypedArray()
                )
            )
            else -> setLocale(locales.first())
        }
    })
    else -> this
}
