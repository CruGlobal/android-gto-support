package org.ccci.gto.android.common.support.v4.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WeakMultiKeyLruCache<K, V> extends MultiKeyLruCache<K, V> {
    final Map<K, WeakReference<V>> mBackup = new HashMap<>();

    public WeakMultiKeyLruCache(final int maxSize) {
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
    protected final V createMulti(final K key) {
        final WeakReference<V> ref = mBackup.remove(key);
        if (ref != null) {
            return ref.get();
        }
        return super.createMulti(key);
    }
}
