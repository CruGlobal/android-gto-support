@file:JvmName("BundleUtils")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import java.util.Locale
import org.jetbrains.annotations.Contract

// region Enums

fun Bundle.putEnum(key: String?, value: Enum<*>?) = putString(key, value?.name)

@JvmOverloads
@Contract("_, _, _, !null -> !null")
fun <T : Enum<T>> Bundle.getEnum(type: Class<T>, key: String?, defValue: T? = null): T? {
    return try {
        getString(key)?.let { java.lang.Enum.valueOf<T>(type, it) } ?: defValue
    } catch (e: IllegalArgumentException) {
        defValue
    }
}

@Contract("_, _, !null -> !null")
inline fun <reified T : Enum<T>> Bundle.getEnum(key: String?, defValue: T? = null) =
    getEnum(T::class.java, key, defValue)

// endregion Enums

// region Locales
@Deprecated(
    "Since v3.11.2, use BundleKt.putLocale() instead",
    ReplaceWith("putLocale(key, locale)"),
    DeprecationLevel.ERROR
)
@JvmName("putLocale")
@Suppress("FunctionName")
internal fun Bundle.`-putLocale`(key: String?, locale: Locale?) = putLocale(key, locale)

@Deprecated(
    "Since v3.11.2, use BundleKt.getLocale() instead",
    ReplaceWith("getLocale(key, defValue)"),
    DeprecationLevel.ERROR
)
@JvmOverloads
@JvmName("getLocale")
@Suppress("FunctionName")
fun Bundle.`-getLocale`(key: String?, defValue: Locale? = null) = getLocale(key, defValue)

@Deprecated(
    "Since v3.11.2, use BundleKt.putLocaleArray() instead",
    ReplaceWith("putLocaleArray(key, locales, singleString)"),
    DeprecationLevel.ERROR
)
@JvmOverloads
@JvmName("putLocaleArray")
@Suppress("FunctionName")
fun Bundle.`-putLocaleArray`(key: String?, locales: Array<Locale>?, singleString: Boolean = false) =
    putLocaleArray(key, locales, singleString)

@Deprecated(
    "Since v3.11.2, use BundleKt.getLocaleArray() instead",
    ReplaceWith("getLocaleArray(key)"),
    DeprecationLevel.ERROR
)
@JvmName("getLocaleArray")
@Suppress("FunctionName")
fun Bundle.`-getLocaleArray`(key: String?) = getLocaleArray(key)
// endregion Locales
