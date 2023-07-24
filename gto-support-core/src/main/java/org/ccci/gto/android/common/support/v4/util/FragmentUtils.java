package org.ccci.gto.android.common.support.v4.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @deprecated Since v4.1.0, use {@link org.ccci.gto.android.common.androidx.fragment.app.FragmentKt} instead.
 */
@Deprecated
public final class FragmentUtils {
    private FragmentUtils() {}

    /**
     * @deprecated
     *    Since v4.1.0, use {@link org.ccci.gto.android.common.androidx.fragment.app.FragmentKt#findAncestorFragment()}
     *    instead.
     */
    @Deprecated
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

    /**
     * @deprecated
     *    Since v4.1.0, use {@link org.ccci.gto.android.common.androidx.fragment.app.FragmentKt#findListener()} instead.
     */
    @Deprecated
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
