@file:JvmName("CollectionUtils")

package org.ccci.gto.android.common.jsonapi.util

internal fun <T : Collection<*>> newCollection(type: Class<T>): T {
    // try creating the collection via reflection, suppress exceptions to allow generic types to be used.
    try {
        return type.newInstance()
    } catch (ignored: Throwable) {
    }

    // try using some generic collection type
    return when {
        type.isAssignableFrom(ArrayList::class.java) -> ArrayList<Any?>() as T
        type.isAssignableFrom(HashSet::class.java) -> HashSet<Any?>() as T
        else -> throw IllegalArgumentException("$type is not a supported Collection type, try something more generic")
    }
}
