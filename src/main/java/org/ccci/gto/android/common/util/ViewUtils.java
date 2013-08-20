package org.ccci.gto.android.common.util;

import android.view.View;

public final class ViewUtils {
    public static <T> T findView(final View root, final Class<T> clazz, final int id) {
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }
}
