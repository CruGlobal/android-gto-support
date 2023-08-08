package org.ccci.gto.android.common.util.content

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import java.util.Locale
import org.ccci.gto.android.common.util.os.locales

fun Context.localize(vararg locales: Locale, includeExisting: Boolean = true): Context = when {
    locales.isEmpty() -> this
    else -> createConfigurationContext(
        Configuration().apply {
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
        }
    )
}
fun Context.localizeIfPossible(locale: Locale?) = locale?.let { localize(it) } ?: this

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
