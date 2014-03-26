package org.ccci.gto.android.common.support.v4.fragment;

import android.support.v4.app.Fragment;

import org.ccci.gto.android.common.support.v4.util.FragmentUtils;

public abstract class AbstractFragment extends Fragment {
    protected final <T> T findView(final Class<T> clazz, final int id) {
        return FragmentUtils.findView(this, clazz, id);
    }

    protected final <T> T getAncestorFragment(final Class<T> clazz) {
        return FragmentUtils.getAncestorFragment(this, clazz);
    }

    protected final <T> T getListener(final Class<T> clazz) {
        return FragmentUtils.getListener(this, clazz);
    }
}
