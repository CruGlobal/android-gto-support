package org.ccci.gto.android.common.support.v4.fragment;

import android.support.v4.app.DialogFragment;

import org.ccci.gto.android.common.support.v4.util.FragmentUtils;

public class AbstractDialogFragment extends DialogFragment {
    protected final <T> T findView(final Class<T> clazz, final int id) {
        return FragmentUtils.findView(this, clazz, id);
    }

    protected final <T> T getListener(final Class<T> clazz) {
        return FragmentUtils.getListener(this, clazz);
    }
}
