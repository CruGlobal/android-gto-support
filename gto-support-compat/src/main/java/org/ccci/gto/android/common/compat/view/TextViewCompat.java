package org.ccci.gto.android.common.compat.view;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * @deprecated Since v3.10.0, all methods have been deprecated.
 */
@Deprecated
public class TextViewCompat {
    /**
     * @deprecated Since v3.10.0, use
     *             {@link org.ccci.gto.android.common.util.widget.TextViewKt#getTypefaceStyle(TextView)} from
     *             gto-support-util instead.
     */
    @Deprecated
    public static int getTypefaceStyle(@NonNull final TextView textView) {
        final Typeface tf = textView.getTypeface();
        return tf != null ? tf.getStyle() : Typeface.NORMAL;
    }
}
