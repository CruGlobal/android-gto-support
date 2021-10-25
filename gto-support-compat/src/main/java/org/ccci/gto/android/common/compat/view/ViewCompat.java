package org.ccci.gto.android.common.compat.view;

import android.view.View;

import androidx.annotation.NonNull;

public final class ViewCompat {
    /**
     * @deprecated Since v3.10.0, use {@link View#setClipToOutline(boolean)} directly.
     */
    @Deprecated
    public static void setClipToOutline(@NonNull final View view, final boolean clip) {
        view.setClipToOutline(clip);
    }
}
