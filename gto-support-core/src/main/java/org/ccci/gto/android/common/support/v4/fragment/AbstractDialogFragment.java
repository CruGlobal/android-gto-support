package org.ccci.gto.android.common.support.v4.fragment;

import android.app.Dialog;

import org.ccci.gto.android.common.support.v4.util.FragmentUtils;

import androidx.fragment.app.DialogFragment;

public class AbstractDialogFragment extends DialogFragment {

    /* BEGIN lifecycle */

    @Override
    public void onDestroyView() {
        // Work around bug:
        // http://code.google.com/p/android/issues/detail?id=17423
        final Dialog dialog = this.getDialog();
        if ((dialog != null) && this.getRetainInstance()) {
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }

    /* END lifecycle */

    /**
     * @deprecated Since v3.0.1, use FragmentUtils directly
     */
    @Deprecated
    protected final <T> T findView(final Class<T> clazz, final int id) {
        return FragmentUtils.findView(this, clazz, id);
    }

    /**
     * @deprecated Since v3.0.1, use FragmentUtils directly
     */
    @Deprecated
    protected final <T> T getListener(final Class<T> clazz) {
        return FragmentUtils.getListener(this, clazz);
    }
}
