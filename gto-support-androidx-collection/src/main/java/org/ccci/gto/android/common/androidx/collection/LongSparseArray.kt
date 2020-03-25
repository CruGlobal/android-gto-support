package org.ccci.gto.android.common.androidx.collection

import androidx.collection.LongSparseArray

fun <T> LongSparseArray<T>.mutableKeyIterator(): MutableIterator<Long> =
    object : LongIterator(), MutableIterator<Long> {
        var index = 0
        var nextCalled = false
        override fun hasNext() = index < size()
        override fun nextLong() = keyAt(index++).also { nextCalled = true }
        override fun remove() {
            check(nextCalled) {
                "next() was not called or remove() was called more than once for the most recent next() call"
            }

            removeAt(--index)
            nextCalled = false
        }
    }
