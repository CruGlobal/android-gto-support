package org.ccci.gto.android.common.support.v4.util;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import java.util.HashMap;
import java.util.Map;

public class IdUtils {
    // no need to use AtomicLong because it is only referenced in blocks synchronized on IDS_REVERSE
    private static volatile long NEXT_ID = 1;
    private static final LongSparseArray<Object> IDS = new LongSparseArray<>();
    private static final Map<Object, Long> IDS_REVERSE = new HashMap<>();

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> T convertId(final long id) {
        synchronized (IDS) {
            return (T) IDS.get(id);
        }
    }

    /**
     * Convert an arbitrary object to a long id. The arbitrary objects are kept in an internal mapping table, so use
     * simple immutable objects only to prevent leaking memory.
     *
     * @param id  arbitrary object
     * @param <T> any simple immutable class
     * @return unique id for arbitrary object
     */
    public static <T> long convertId(@NonNull final T id) {
        synchronized (IDS_REVERSE) {
            Long rawId = IDS_REVERSE.get(id);
            if (rawId == null) {
                rawId = NEXT_ID++;
                synchronized (IDS) {
                    IDS.put(rawId, id);
                }
                IDS_REVERSE.put(id, rawId);
            }
            return rawId;
        }
    }
}
