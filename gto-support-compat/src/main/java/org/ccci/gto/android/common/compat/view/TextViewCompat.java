package org.ccci.gto.android.common.compat.view;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TextViewCompat {
    public static int getTypefaceStyle(@NonNull final TextView textView) {
        final Typeface tf = textView.getTypeface();
        return tf != null ? tf.getStyle() : Typeface.NORMAL;
    }
}
