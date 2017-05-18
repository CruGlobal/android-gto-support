package org.ccci.gto.android.common.support.v4.util;

import android.support.v4.util.LruCache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WeakLruCache<K, V> extends LruCache<K, V> {
    private final Map<K, WeakReference<V>> mBackup = new HashMap<>();

    public WeakLruCache(final int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(final boolean evicted, final K key, final V oldValue, final V newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (evicted) {
            mBackup.put(key, new WeakReference<>(oldValue));
        }
    }

    @Override
    protected final V create(final K key) {
        final WeakReference<V> ref = mBackup.remove(key);
        if (ref != null) {
            return ref.get();
        }
        return super.create(key);
    }
}
