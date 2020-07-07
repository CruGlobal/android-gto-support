package com.google.android.material.textfield;

import android.content.res.ColorStateList;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.material.textfield.TextInputLayoutKt;

/**
 * @deprecated Since v3.6.1, use extension methods instead.
 */
@Deprecated
public final class TextInputLayoutUtils {
    /**
     * @deprecated Since v3.6.1, use TextInputLayout.setFocusedTextColor extension method instead.
     */
    @Deprecated
    public static void setFocusedTextColor(@NonNull final TextInputLayout layout, @ColorInt final int color) {
        TextInputLayoutKt.setFocusedTextColor(layout, color);
    }

    /**
     * @deprecated Since v3.6.1, use TextInputLayout.setFocusedTextColor extension method instead.
     */
    @Deprecated
    public static void setFocusedTextColor(@NonNull final TextInputLayout layout,
                                           @Nullable final ColorStateList colors) {
        TextInputLayoutKt.setFocusedTextColor(layout, colors);
    }
}
