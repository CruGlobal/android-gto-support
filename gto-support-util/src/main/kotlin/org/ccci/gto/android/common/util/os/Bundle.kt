@file:JvmMultifileClass
@file:JvmName("BundleKt")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable
import org.ccci.gto.android.common.compat.os.getParcelableArrayCompat

// region Parcelables
inline fun <reified T : Parcelable> Bundle.getTypedParcelableArray(key: String?) =
    getParcelableArrayCompat(key, T::class.java)
// endregion Parcelables
