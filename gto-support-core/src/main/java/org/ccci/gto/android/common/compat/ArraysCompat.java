package org.ccci.gto.android.common.compat;

import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * @deprecated Since v0.9.0, There are no useful compatibility methods in this class currently.
 */
@Deprecated
public class ArraysCompat {
    /**
     * @deprecated Since v0.9.0, use {@link Arrays#copyOfRange(Object[], int, int)} instead
     */
    @NonNull
    @Deprecated
    public static <T> T[] copyOfRange(@NonNull final T[] original, final int start, final int end) {
        return Arrays.copyOfRange(original, start, end);
    }
}
