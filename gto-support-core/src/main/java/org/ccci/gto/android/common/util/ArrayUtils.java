package org.ccci.gto.android.common.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;

public class ArrayUtils {
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> T[] merge(@NonNull final Class<T> clazz, @Nullable final T[] a, @Nullable final T[] b) {
        // determine length of both input arrays
        final int aLen = a != null ? a.length : 0;
        final int bLen = b != null ? b.length : 0;

        // short-circuit for simple corner cases
        if (aLen == 0 && bLen > 0) {
            return b;
        } else if (aLen > 0 && bLen == 0) {
            return a;
        }

        // generate new array, only copy items if any exist in source arrays
        final T[] c = (T[]) Array.newInstance(clazz, aLen + bLen);
        if (aLen > 0) {
            System.arraycopy(a, 0, c, 0, aLen);
        }
        if (bLen > 0) {
            System.arraycopy(b, 0, c, aLen, bLen);
        }

        // return new array
        return c;
    }
}
