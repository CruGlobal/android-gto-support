@file:JvmName("BundleUtils")

package org.ccci.gto.android.common.util.os

import android.os.Bundle
import org.jetbrains.annotations.Contract

// region Enums

fun Bundle.putEnum(key: String?, value: Enum<*>?) = putString(key, value?.name)

@JvmOverloads
@Contract("_, _, _, !null -> !null")
fun <T : Enum<T>> Bundle.getEnum(type: Class<T>, key: String?, defValue: T? = null): T? {
    return try {
        getString(key)?.let { java.lang.Enum.valueOf<T>(type, it) } ?: defValue
    } catch (e: IllegalArgumentException) {
        defValue
    }
}

@Contract("_, _, !null -> !null")
inline fun <reified T : Enum<T>> Bundle.getEnum(key: String?, defValue: T? = null) =
    getEnum(T::class.java, key, defValue)

// endregion Enums
