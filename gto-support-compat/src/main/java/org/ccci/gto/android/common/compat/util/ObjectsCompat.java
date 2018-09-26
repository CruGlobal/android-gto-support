package org.ccci.gto.android.common.compat.util;

import androidx.annotation.Nullable;

public final class ObjectsCompat {
    public static boolean equals(@Nullable final Object a, @Nullable final Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
