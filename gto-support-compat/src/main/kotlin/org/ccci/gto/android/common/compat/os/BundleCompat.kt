@file:JvmName("BundleCompat")

package org.ccci.gto.android.common.compat.os

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable

private val COMPAT = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> TiramisuBundleCompatMethods()
    else -> BaseBundleCompatMethods()
}

@JvmName("getParcelable")
fun <T> Bundle.getParcelableCompat(key: String?, clazz: Class<T>) = COMPAT.getParcelable(this, key, clazz)
@JvmName("getParcelableArray")
fun <T> Bundle.getParcelableArrayCompat(key: String?, clazz: Class<T>) = COMPAT.getParcelableArray(this, key, clazz)
@JvmName("getSerializable")
fun <T : Serializable> Bundle.getSerializableCompat(key: String?, clazz: Class<T>) =
    COMPAT.getSerializable(this, key, clazz)

private sealed interface BundleCompatMethods {
    fun <T> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>): T?
    fun <T> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>?
    fun <T : Serializable> getSerializable(bundle: Bundle, key: String?, clazz: Class<T>): T?
}

private open class BaseBundleCompatMethods : BundleCompatMethods {
    override fun <T> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw = bundle.getParcelable<Parcelable>(key)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }

    override fun <T> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>? {
        @Suppress("DEPRECATION")
        val raw = bundle.getParcelableArray(key) ?: return null
        @Suppress("UNCHECKED_CAST")
        val arr = java.lang.reflect.Array.newInstance(clazz, raw.size) as Array<T?>
        System.arraycopy(raw, 0, arr, 0, raw.size)
        return arr
    }

    override fun <T : Serializable> getSerializable(bundle: Bundle, key: String?, clazz: Class<T>): T? {
        @Suppress("DEPRECATION")
        val raw = bundle.getSerializable(key)
        return if (clazz.isInstance(raw)) clazz.cast(raw) else null
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class TiramisuBundleCompatMethods : BaseBundleCompatMethods() {
    override fun <T> getParcelable(bundle: Bundle, key: String?, clazz: Class<T>) = bundle.getParcelable(key, clazz)
    override fun <T> getParcelableArray(bundle: Bundle, key: String?, clazz: Class<T>): Array<T?>? =
        bundle.getParcelableArray(key, clazz)
    override fun <T : Serializable> getSerializable(bundle: Bundle, key: String?, clazz: Class<T>) =
        bundle.getSerializable(key, clazz)
}
