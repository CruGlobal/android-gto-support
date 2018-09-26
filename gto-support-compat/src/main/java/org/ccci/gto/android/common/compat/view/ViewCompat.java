package org.ccci.gto.android.common.compat.view;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

public final class ViewCompat {
    public static void setClipToOutline(@NonNull final View view, final boolean clip) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setClipToOutline(clip);
        }
    }
}
