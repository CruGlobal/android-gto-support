package org.ccci.gto.android.common.support.v4.util;

/**
 * @deprecated Since v3.7.0, use {@link org.ccci.gto.android.common.util.MultiKeyLruCache} instead.
 */
@Deprecated
public class MultiKeyLruCache<K, V> extends org.ccci.gto.android.common.util.MultiKeyLruCache<K, V> {
    public MultiKeyLruCache(final int maxSize) {
        super(maxSize);
    }
}
