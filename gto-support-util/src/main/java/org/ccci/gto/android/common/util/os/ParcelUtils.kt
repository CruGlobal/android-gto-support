@file:JvmName("ParcelUtils")
package org.ccci.gto.android.common.util.os

import android.os.Parcel

@Deprecated(
    "Since v3.4.0, use ParcelCompat from androidx.core instead",
    ReplaceWith("ParcelCompat.writeBoolean(this, value)", "androidx.core.os.ParcelCompat")
)
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Parcel.writeBoolean(value: Boolean) = writeInt(if (value) 1 else 0)

@Deprecated(
    "Since v3.4.0, use ParcelCompat from androidx.core instead",
    ReplaceWith("ParcelCompat.readBoolean(this)", "androidx.core.os.ParcelCompat")
)
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Parcel.readBoolean() = readInt() != 0
