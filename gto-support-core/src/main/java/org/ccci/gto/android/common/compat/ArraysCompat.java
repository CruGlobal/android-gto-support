package org.ccci.gto.android.common.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArraysCompat {
    @NonNull
    private static final Compat COMPAT =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD ? new GingerbreadCompat() : new FroyoCompat();

    @NonNull
    public static <T> T[] copyOfRange(@NonNull final T[] original, final int start, final int end) {
        return COMPAT.copyOfRange(original, start, end);
    }

    private interface Compat {
        @NonNull
        <T> T[] copyOfRange(T[] original, int start, int end);
    }

    private static class FroyoCompat implements Compat {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] copyOfRange(@NonNull final T[] original, final int start, final int end) {
            int originalLength = original.length; // For exception priority compatibility.
            if (start > end) {
                throw new IllegalArgumentException();
            }
            if (start < 0 || start > originalLength) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int resultLength = end - start;
            int copyLength = Math.min(resultLength, originalLength - start);
            T[] result = (T[]) Array.newInstance(original.getClass().getComponentType(), resultLength);
            System.arraycopy(original, start, result, 0, copyLength);
            return result;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static class GingerbreadCompat extends FroyoCompat {
        @NonNull
        @Override
        public <T> T[] copyOfRange(T[] original, int start, int end) {
            return Arrays.copyOfRange(original, start, end);
        }
    }
}
