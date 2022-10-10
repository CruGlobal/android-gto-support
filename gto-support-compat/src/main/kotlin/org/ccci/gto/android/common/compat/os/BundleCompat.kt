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

@JvmName("getParcelable")
fun <T : Parcelable> Bundle.getParcelableCompat(key: String?, clazz: Class<T>) = COMPAT.getParcelable(this, key, clazz)
@JvmName("getParcelableArray")
fun <T : Parcelable> Bundle.getParcelableArrayCompat(key: String?, clazz: Class<T>) =
    COMPAT.getParcelableArray(this, key, clazz)

private sealed interface BundleCompatMethods {
    fun <T : Parcelable> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>): T?
    fun <T : Parcelable> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>?
}

private open class BaseBundleCompatMethods : BundleCompatMethods {
    override fun <T : Parcelable> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw = bundle.getParcelable<T>(key)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }

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
    override fun <T : Parcelable> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>) =
        bundle.getParcelable(key, clazz)
    override fun <T : Parcelable> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>? =
        bundle.getParcelableArray(key, clazz)
}
