package org.ccci.gto.android.common.util

import android.util.LruCache

open class MultiKeyLruCache<K, V : Any>(maxSize: Int) : LruCache<K, V>(maxSize) {
    private val copies = mutableMapOf<V, Int>()
    private var sizeGap = 0

    fun putMulti(key: K, value: V): V? {
        incCount(key, value)
        return put(key, value)
    }

    final override fun create(key: K): V? = createMulti(key)?.also { incCount(key, it) }
    protected open fun createMulti(key: K): V? = null

    override fun entryRemoved(evicted: Boolean, key: K, oldValue: V?, newValue: V?) {
        if (oldValue != null) decCount(key, oldValue)
        super.entryRemoved(evicted, key, oldValue, newValue)
    }

    final override fun trimToSize(maxSize: Int) {
        // loop until the size gap stabilizes
        var gap: Int
        do {
            gap = sizeGap
            super.trimToSize(maxSize + sizeGap)
        } while (gap != sizeGap)
    }

    private fun incCount(key: K, value: V) {
        synchronized(copies) {
            val count = (copies[value] ?: 0) + 1
            if (count > 1) sizeGap += sizeOf(key, value).coerceAtLeast(0)
            copies[value] = count
        }
    }

    private fun decCount(key: K, value: V) {
        synchronized(copies) {
            val count = (copies[value] ?: 0) - 1
            when {
                count > 0 -> {
                    sizeGap = (sizeGap - sizeOf(key, value).coerceAtLeast(0)).coerceAtLeast(0)
                    copies[value] = count
                }

                else -> copies.remove(value)
            }
        }
    }
}
