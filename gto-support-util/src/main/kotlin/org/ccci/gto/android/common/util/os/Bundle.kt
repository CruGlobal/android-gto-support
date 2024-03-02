@file:JvmMultifileClass
@file:JvmName("BundleKt")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable
import org.ccci.gto.android.common.compat.os.getParcelableArrayCompat

// region equalsBundle()
@Suppress("DEPRECATION")
infix fun Bundle?.equalsBundle(other: Bundle?) = when {
    this === other -> true
    this == null -> false
    other == null -> false
    size() != other.size() -> false
    keySet() != other.keySet() -> false
    keySet().any { get(it) != other.get(it) } -> false
    else -> true
}
// endregion equalsBundle()

// region Parcelables
inline fun <reified T : Parcelable> Bundle.getTypedParcelableArray(key: String?) =
    getParcelableArrayCompat(key, T::class.java)
// endregion Parcelables
