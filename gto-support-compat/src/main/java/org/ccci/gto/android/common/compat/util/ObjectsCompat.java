package org.ccci.gto.android.common.compat.util;

import androidx.annotation.Nullable;

/**
 * @deprecated Since v3.2.0, all the methods in this class are deprecated.
 */
@Deprecated
public final class ObjectsCompat {
    /**
     * @deprecated Since v3.2.0, code utilizing this should be converted to Kotlin.
     */
    @Deprecated
    public static boolean equals(@Nullable final Object a, @Nullable final Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
