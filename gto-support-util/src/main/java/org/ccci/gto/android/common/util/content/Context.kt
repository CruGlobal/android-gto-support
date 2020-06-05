package org.ccci.gto.android.common.util.content

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

fun Context.localize(vararg locales: Locale): Context = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> createConfigurationContext(Configuration().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setLocales(LocaleList(*locales))
            else -> setLocale(locales.firstOrNull())
        }
    })
    else -> this
}
