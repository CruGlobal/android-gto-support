package org.ccci.gto.android.common.support.v4.util;

import androidx.collection.LruCache;

import java.util.HashMap;
import java.util.Map;

public class MultiKeyLruCache<K, V> extends LruCache<K, V> {
    private final Map<V, Integer> mCopies = new HashMap<>();
    private int mSizeGap = 0;

    public MultiKeyLruCache(final int maxSize) {
        super(maxSize);
    }

    public V putMulti(final K key, final V value) {
        incCount(key, value);
        return this.put(key, value);
    }

    @Override
    protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
        decCount(key, oldValue);
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    @Override
    protected final V create(final K key) {
        final V value = this.createMulti(key);
        if (value != null) {
            incCount(key, value);
        }
        return value;
    }

    protected V createMulti(final K key) {
        return null;
    }

    @Override
    public void trimToSize(final int maxSize) {
        // we need to loop until size gap stabilizes
        int gap = -1;
        while (mSizeGap != gap) {
            gap = mSizeGap;
            super.trimToSize(maxSize + mSizeGap);
        }
    }

    private void incCount(final K key, final V value) {
        synchronized (mCopies) {
            Integer count = mCopies.get(value);
            count = count != null ? count + 1 : 1;
            mCopies.put(value, count);

            if (count > 1) {
                final int size = this.sizeOf(key, value);
                if (size >= 0) {
                    mSizeGap += size;
                }
            }
        }
    }

    private void decCount(final K key, final V value) {
        synchronized (mCopies) {
            Integer count = mCopies.get(value);
            count = count != null ? count - 1 : 0;
            if (count <= 0) {
                mCopies.remove(value);
            } else {
                mCopies.put(value, count);

                final int size = this.sizeOf(key, value);
                if (size >= 0) {
                    mSizeGap -= size < mSizeGap ? size : mSizeGap;
                }
            }
        }
    }
}
