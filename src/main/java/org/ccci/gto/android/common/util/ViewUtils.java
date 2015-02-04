package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public final class ViewUtils {
    @Nullable
    public static <T> T findView(@Nullable final View root, @NonNull final Class<T> clazz, final int id) {
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }
}
