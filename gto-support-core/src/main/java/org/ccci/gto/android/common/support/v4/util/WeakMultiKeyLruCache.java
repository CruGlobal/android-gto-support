package org.ccci.gto.android.common.support.v4.util;

import android.support.v4.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.Map;

public class WeakMultiKeyLruCache<K, V> extends MultiKeyLruCache<K, V> {
    private final Map<K, WeakReference<V>> mBackup = new ArrayMap<>();

    public WeakMultiKeyLruCache(final int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(final boolean evicted, final K key, final V oldValue, final V newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (evicted) {
            synchronized (mBackup) {
                mBackup.put(key, new WeakReference<>(oldValue));
            }
        }
    }

    @Override
    protected final V createMulti(final K key) {
        final WeakReference<V> ref;
        synchronized (mBackup) {
            ref = mBackup.remove(key);
        }

        if (ref != null) {
            return ref.get();
        }
        return super.createMulti(key);
    }
}
