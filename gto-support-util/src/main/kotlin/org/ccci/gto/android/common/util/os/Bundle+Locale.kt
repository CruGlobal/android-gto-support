@file:JvmMultifileClass
@file:JvmName("BundleKt")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import java.util.Locale

@JvmOverloads
fun Bundle.getLocale(key: String?, defValue: Locale? = null) =
    getString(key)?.let { Locale.forLanguageTag(it) } ?: defValue
@JvmName("getNonNullLocale")
fun Bundle.getLocale(key: String?, defValue: Locale) = getString(key)?.let { Locale.forLanguageTag(it) } ?: defValue
fun Bundle.putLocale(key: String?, locale: Locale?) = putString(key, locale?.toLanguageTag())

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

fun Bundle.getLocaleArray(key: String?) = (getStringArray(key) ?: getString(key)?.split(",")?.toTypedArray())
    ?.map { it?.let { Locale.forLanguageTag(it) } }?.toTypedArray()
