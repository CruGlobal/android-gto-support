package org.ccci.gto.android.common.support.v4.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public final class FragmentUtils {
    private FragmentUtils() {}

    @Nullable
    public static <T> T findView(@NonNull final Fragment fragment, @NonNull final Class<T> clazz, final int id) {
        final View root = fragment.getView();
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T getAncestorFragment(@NonNull final Fragment fragment, @NonNull final Class<T> clazz) {
        Fragment parent = fragment.getParentFragment();
        while (parent != null) {
            if (clazz.isInstance(parent)) {
                return clazz.cast(parent);
            }
            parent = parent.getParentFragment();
        }
        return null;
    }

    @Nullable
    public static <T> T getListener(@NonNull final Fragment fragment, @NonNull final Class<T> clazz) {
        final Fragment frag = fragment.getParentFragment();
        if (clazz.isInstance(frag)) {
            return clazz.cast(frag);
        }

        final FragmentActivity activity = fragment.getActivity();
        if (clazz.isInstance(activity)) {
            return clazz.cast(activity);
        }

        return null;
    }
}
