@file:JvmMultifileClass
@file:JvmName("BundleKt")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable
import org.ccci.gto.android.common.compat.os.getParcelableArrayCompat

// region Parcelables
@Deprecated(
    "Since v3.13.0, use Bundle.getParcelableArrayCompat(key, clazz) instead.",
    ReplaceWith(
        "getParcelableArrayCompat(key, clazz)",
        "org.ccci.gto.android.common.compat.os.getParcelableArrayCompat"
    )
)
fun <T : Parcelable> Bundle.getParcelableArray(key: String?, clazz: Class<T>) = getParcelableArrayCompat(key, clazz)

inline fun <reified T : Parcelable> Bundle.getTypedParcelableArray(key: String?) =
    getParcelableArrayCompat(key, T::class.java)
// endregion Parcelables
