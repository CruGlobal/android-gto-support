package org.ccci.gto.android.common.support.v4.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public final class FragmentUtils {
    private FragmentUtils() {
    }

    public static final <T> T findView(final Fragment fragment, final Class<T> clazz, final int id) {
        final View root = fragment.getView();
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }

    public static final <T> T getListener(final Fragment fragment, final Class<T> clazz) {
        final Fragment frag = fragment.getParentFragment();
        if(clazz.isInstance(frag)) {
            return clazz.cast(frag);
        }

        final FragmentActivity activity = fragment.getActivity();
        if(clazz.isInstance(activity)) {
            return clazz.cast(activity);
        }

        return null;
    }
}
