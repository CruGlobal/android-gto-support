package org.ccci.gto.android.common.util

import timber.log.Timber

inline fun <reified T> getDeclaredFieldOrNull(name: String) = try {
    T::class.java.getDeclaredField(name).apply { isAccessible = true }
} catch (e: Exception) {
    Timber.tag("ReflectionUtils").e(e, "error resolving ${T::class.java.simpleName}.$name")
    null
}

inline fun <reified T> getDeclaredMethodOrNull(name: String, vararg parameterTypes: Class<*>) = try {
    T::class.java.getDeclaredMethod(name, *parameterTypes).apply { isAccessible = true }
} catch (e: Exception) {
    Timber.tag("ReflectionUtils").e(e, "error resolving ${T::class.java.simpleName}.$name()")
    null
}
