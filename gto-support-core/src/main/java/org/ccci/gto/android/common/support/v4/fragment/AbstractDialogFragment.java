package org.ccci.gto.android.common.support.v4.fragment;

import android.app.Dialog;

import androidx.fragment.app.DialogFragment;

/**
 * @deprecated Since v3.11.0, this worked around a bug with functionality that has now been deprecated.
 */
@Deprecated
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
}
