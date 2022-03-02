package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable
import java.util.Locale
import org.jetbrains.annotations.Contract

// region Locales
fun Bundle.putLocale(key: String?, locale: Locale?) = putString(key, locale?.toLanguageTag())
fun Bundle.getLocale(key: String?) = getString(key)?.let { Locale.forLanguageTag(it) }

/**
 * Store an array of Locales in the provided Bundle
 *
 * @receiver The bundle to store the locale array in
 * @param key The key to store the locale array under
 * @param locales The locales being put in the bundle
 * @param singleString Flag indicating if the locale array should be stored as a single string
 */
@JvmOverloads
fun Bundle.putLocaleArray(key: String?, locales: Array<Locale>?, singleString: Boolean = false) {
    val tags = locales?.map { it.toLanguageTag() }?.toTypedArray()

    if (singleString) {
        putString(key, tags?.joinToString(","))
    } else {
        putStringArray(key, tags)
    }
}

fun Bundle.getLocaleArray(key: String?) =
    (getStringArray(key) ?: getString(key)?.split(",")?.toTypedArray())
        ?.map { it?.let { Locale.forLanguageTag(it) } }?.toTypedArray()
// endregion Locales

// region Parcelables
@Suppress("UNCHECKED_CAST")
fun <T : Parcelable> Bundle.getParcelableArray(key: String?, clazz: Class<T>) =
    getParcelableArray(key)?.let {
        val arr = java.lang.reflect.Array.newInstance(clazz, it.size) as Array<T?>
        System.arraycopy(it, 0, arr, 0, it.size)
        arr
    }

inline fun <reified T : Parcelable> Bundle.getTypedParcelableArray(key: String?) =
    getParcelableArray(key)?.let { it: Array<Parcelable?> ->
        val arr = arrayOfNulls<T>(it.size)
        System.arraycopy(it, 0, arr, 0, it.size)
        arr
    }
// endregion Parcelables
