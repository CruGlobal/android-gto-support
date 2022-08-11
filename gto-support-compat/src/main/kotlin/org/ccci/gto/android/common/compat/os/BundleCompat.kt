@file:JvmName("BundleCompat")

package org.ccci.gto.android.common.compat.os

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

private val COMPAT = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> TiramisuBundleCompatMethods()
    else -> BaseBundleCompatMethods()
}

@JvmName("getParcelableArray")
fun <T : Parcelable> Bundle.getParcelableArrayCompat(key: String?, clazz: Class<T>) =
    COMPAT.getParcelableArray(this, key, clazz)

private sealed interface BundleCompatMethods {
    fun <T : Parcelable> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>?
}

private open class BaseBundleCompatMethods : BundleCompatMethods {
    override fun <T : Parcelable> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>? {
        @Suppress("DEPRECATION")
        val raw = bundle.getParcelableArray(key) ?: return null
        @Suppress("UNCHECKED_CAST")
        val arr = java.lang.reflect.Array.newInstance(clazz, raw.size) as Array<T?>
        System.arraycopy(raw, 0, arr, 0, raw.size)
        return arr
    }
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
private class TiramisuBundleCompatMethods : BaseBundleCompatMethods() {
    override fun <T : Parcelable> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>? =
        bundle.getParcelableArray(key, clazz)
}
