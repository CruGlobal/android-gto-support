package org.ccci.gto.android.common.support.v4.util

import androidx.collection.ArrayMap
import androidx.collection.LruCache
import java.lang.ref.WeakReference

/**
 * LruCache that will maintain a weak reference to evicted items to try and reuse the items if they are still alive in
 * memory. Due to the final behavior of remove you should use [WeakLruCache.removeWeak] instead of [LruCache.remove] to
 * ensure an item is actually removed from the cache.
 */
class WeakLruCache<K : Any, V : Any>(maxSize: Int) : LruCache<K, V>(maxSize) {
    private val backup: MutableMap<K, WeakReference<V>> = ArrayMap()

    override fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {
        super.entryRemoved(evicted, key, oldValue, newValue)
        synchronized(backup) {
            if (evicted) {
                backup.put(key, WeakReference(oldValue))
            } else {
                backup.remove(key)
            }
        }
    }

    override fun create(key: K): V? = synchronized(backup) { backup.remove(key) }?.get() ?: super.create(key)
    fun removeWeak(key: K): V? = remove(key) ?: synchronized(backup) { backup.remove(key)?.get() }
}
