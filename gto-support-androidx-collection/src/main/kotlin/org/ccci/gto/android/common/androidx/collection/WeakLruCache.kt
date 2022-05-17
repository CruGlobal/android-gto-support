package org.ccci.gto.android.common.androidx.collection

import androidx.collection.LruCache
import androidx.collection.SimpleArrayMap
import java.lang.ref.WeakReference

/**
 * LruCache that will maintain a weak reference to evicted items to try and reuse the items if they are still alive in
 * memory. Due to the final behavior of remove you should use [WeakLruCache.removeWeak] instead of [LruCache.remove] to
 * ensure an item is actually removed from the cache.
 */
class WeakLruCache<K : Any, V : Any>(maxSize: Int) : LruCache<K, V>(maxSize) {
    private val backup = SimpleArrayMap<K, WeakReference<V>>()

    override fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {
        synchronized(backup) {
            if (evicted) {
                backup.put(key, WeakReference(oldValue))
            } else {
                backup.remove(key)
            }
        }
    }

    override fun trimToSize(maxSize: Int) {
        super.trimToSize(maxSize)
        pruneGCedBackup()
    }

    /**
     * This method will prune any backup entries that have been garbage collected
     */
    private fun pruneGCedBackup() = synchronized(backup) {
        for (i in backup.size() - 1 downTo 0) {
            if (backup.valueAt(i).get() == null) backup.removeAt(i)
        }
    }

    override fun create(key: K): V? = synchronized(backup) { backup.remove(key) }?.get()
    fun removeWeak(key: K): V? = remove(key) ?: synchronized(backup) { backup.remove(key)?.get() }
}
