package org.ccci.gto.android.common.util.content

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import java.util.Locale
import org.ccci.gto.android.common.util.os.locales
import org.ccci.gto.android.common.util.os.toTypedArray

fun Context.localize(vararg locales: Locale, includeExisting: Boolean = true): Context = when {
    locales.isEmpty() -> this
    Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> when (val first = locales.first()) {
        resources.configuration.locale -> this
        else -> createConfigurationContext(Configuration().apply { setLocale(first) })
    }
    else -> when (
        val newLocales = LocaleList(
            *LinkedHashSet<Locale>().apply {
                addAll(locales)
                if (includeExisting) addAll(resources.configuration.locales.locales)
            }.toTypedArray(),
        )
    ) {
        resources.configuration.locales -> this
        else -> createConfigurationContext(Configuration().apply { setLocales(newLocales) })
    }
}
fun Context.localizeIfPossible(locale: Locale?) = locale?.let { localize(it) } ?: this

@RequiresApi(Build.VERSION_CODES.N)
fun Context.localize(locales: LocaleList, includeExisting: Boolean = true) = when {
    locales.isEmpty -> this
    else -> localize(*locales.toTypedArray(), includeExisting = includeExisting)
}
@RequiresApi(Build.VERSION_CODES.N)
fun Context.localizeIfPossible(locales: LocaleList?) = locales?.let { localize(locales) } ?: this

// region findActivity()
fun Context.findActivityOrNull(): Activity? = findActivityOrNull<Activity>()
@JvmName("findActivityOrNullInline")
inline fun <reified T : Activity> Context.findActivityOrNull(): T? {
    var context = this
    while (context is ContextWrapper) {
        if (context is T) return context
        context = context.baseContext
    }
    return null
}

fun Context.findActivity(): Activity = findActivity<Activity>()
@JvmName("findActivityInline")
inline fun <reified T : Activity> Context.findActivity(): T =
    findActivityOrNull<T>() ?: error("Context doesn't extend from a ${T::class.simpleName}")
// endregion findActivity()

fun Context.getString(locale: Locale?, @StringRes resId: Int, vararg formatArgs: Any?) =
    localizeIfPossible(locale).getString(resId, *formatArgs)

/**
 * replacement for BuildConfig.DEBUG to allow libraries to check final app debug mode.
 */
val Context.isApplicationDebuggable get() = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
