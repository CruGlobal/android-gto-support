package org.ccci.gto.android.common.util

import java.lang.reflect.Field
import org.ccci.gto.android.common.util.lang.getDeclaredMethodOrNull
import timber.log.Timber

inline fun <reified T> getDeclaredFieldOrNull(name: String) = try {
    T::class.java.getDeclaredField(name).apply { isAccessible = true }
} catch (e: Exception) {
    Timber.tag("ReflectionUtils").e(e, "error resolving ${T::class.java.simpleName}.$name")
    null
}

inline fun <reified T> getDeclaredMethodOrNull(name: String, vararg parameterTypes: Class<*>) =
    T::class.java.getDeclaredMethodOrNull(name, *parameterTypes)

fun Field.getOrNull(obj: Any) = try {
    get(obj)
} catch (e: Exception) {
    Timber.tag("ReflectionUtils").e(e, "error retrieving field value for field: $this")
    null
}
