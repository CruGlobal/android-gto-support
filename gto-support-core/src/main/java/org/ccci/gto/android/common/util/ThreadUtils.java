package org.ccci.gto.android.common.util;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Map;

public final class ThreadUtils {
    private ThreadUtils() {
    }

    public static void assertNotOnUiThread() {
        if (isUiThread()) {
            throw new RuntimeException("unsupported method on UI thread");
        }
    }

    public static void assertOnUiThread() {
        if (!isUiThread()) {
            throw new RuntimeException("method requires UI thread");
        }
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static <K> Object getLock(final Map<K, Object> locks, final K key) {
        synchronized (locks) {
            if (!locks.containsKey(key)) {
                locks.put(key, new Object());
            }
            return locks.get(key);
        }
    }

    public static boolean isUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static final class GenericKey {
        @NonNull
        private final Object[] mKey;

        public GenericKey(@NonNull final Object... key) {
            mKey = key;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GenericKey that = (GenericKey) o;
            return Arrays.deepEquals(mKey, that.mKey);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(mKey);
        }
    }
}
