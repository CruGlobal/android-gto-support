package org.ccci.gto.android.common.util.lang

import timber.log.Timber

private const val TAG = "ClassKt"

fun getClassOrNull(name: String) = try {
    Class.forName(name)
} catch (e: Exception) {
    Timber.tag(TAG).e(e, "error resolving $name")
    null
}

fun Class<*>.getDeclaredMethodOrNull(name: String, vararg parameterTypes: Class<*>) = try {
    getDeclaredMethod(name, *parameterTypes).apply { isAccessible = true }
} catch (e: Exception) {
    Timber.tag(TAG).e(e, "error resolving $simpleName.$name()")
    null
}
