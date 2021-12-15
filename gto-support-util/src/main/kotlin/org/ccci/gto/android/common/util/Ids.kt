package org.ccci.gto.android.common.util

import android.util.LongSparseArray

object Ids {
    // no need to use AtomicLong because it is only referenced in blocks synchronized on IDS_REVERSE
    @Volatile
    private var nextId = 1L

    private val IDS = LongSparseArray<Any>()
    private val IDS_REVERSE = HashMap<Any, Long>()

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> lookup(id: Long) = synchronized(IDS) { IDS[id] as T }

    /**
     * Generate an id for an arbitrary object. The arbitrary objects are kept in an internal mapping table, so use
     * simple immutable objects only to prevent leaking memory.
     *
     * @param obj arbitrary simple immutable object
     * @return unique id for arbitrary object
     */
    @JvmStatic
    fun generate(obj: Any) = synchronized(IDS_REVERSE) {
        IDS_REVERSE[obj] ?: nextId++.also {
            synchronized(IDS) { IDS.put(it, obj) }
            IDS_REVERSE[obj] = it
        }
    }
}
