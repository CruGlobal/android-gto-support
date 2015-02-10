package org.ccci.gto.android.common.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public final class ViewUtils {
    @Nullable
    public static <T> T findView(@Nullable final View root, @NonNull final Class<T> clazz, @IdRes final int id) {
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T findView(@NonNull final Activity activity, @NonNull final Class<T> clazz, @IdRes final int id) {
        final View view = activity.findViewById(id);
        if (clazz.isInstance(view)) {
            return clazz.cast(view);
        }
        return null;
    }
}
