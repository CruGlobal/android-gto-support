package org.ccci.gto.android.common.support.v4.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * LruCache that will maintain a weak reference to evicted items to try and reuse the items if they are still alive in
 * memory. Due to the final behavior of remove you should use {@link WeakLruCache#removeWeak(Object)} instead of {@link
 * LruCache#remove(Object)} to ensure an item is actually removed from the cache.
 */
public class WeakLruCache<K, V> extends LruCache<K, V> {
    private final Map<K, WeakReference<V>> mBackup = new ArrayMap<>();

    public WeakLruCache(final int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(final boolean evicted, final K key, final V oldValue, final V newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        synchronized (mBackup) {
            if (evicted) {
                mBackup.put(key, new WeakReference<>(oldValue));
            } else {
                mBackup.remove(key);
            }
        }
    }

    @Override
    protected final V create(final K key) {
        final WeakReference<V> ref;
        synchronized (mBackup) {
            ref = mBackup.remove(key);
        }

        if (ref != null) {
            return ref.get();
        }
        return super.create(key);
    }

    @Nullable
    public final V removeWeak(@NonNull final K key) {
        final V val = remove(key);
        if (val == null) {
            synchronized (mBackup) {
                final WeakReference<V> ref = mBackup.remove(key);
                return ref != null ? ref.get() : null;
            }
        }
        return val;
    }
}
