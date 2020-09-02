package org.ccci.gto.android.common.util.os

import android.os.Bundle
import android.os.Parcelable

// region Parcelables
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
