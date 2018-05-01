package org.ccci.gto.android.common.util.view;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class TextViewUtils {
    public static void setTypefacePreservingStyle(@NonNull final TextView tv, @Nullable final Typeface typeface) {
        final Typeface tf = tv.getTypeface();
        final int style = tf != null ? tf.getStyle() : Typeface.NORMAL;
        tv.setTypeface(typeface, style);
    }
}
