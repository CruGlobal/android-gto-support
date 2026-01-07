@file:JvmMultifileClass
@file:JvmName("BundleKt")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable
import org.ccci.gto.android.common.compat.os.getParcelableArrayCompat

// region equalsBundle()
@Suppress("DEPRECATION", "ktlint:standard:blank-line-between-when-conditions")
@JvmName("bundleEquals")
infix fun Bundle?.equalsBundle(other: Bundle?) = when {
    this === other -> true
    this == null -> false
    other == null -> false
    size() != other.size() -> false
    keySet() != other.keySet() -> false
    keySet().any {
        val value = get(it)
        val otherValue = other.get(it)
        if (value is Array<*> && otherValue is Array<*>) {
            !value.contentEquals(otherValue)
        } else {
            get(it) != other.get(it)
        }
    } -> false
    else -> true
}
// endregion equalsBundle()

// region Parcelables
inline fun <reified T : Parcelable> Bundle.getTypedParcelableArray(key: String?) =
    getParcelableArrayCompat(key, T::class.java)
// endregion Parcelables
