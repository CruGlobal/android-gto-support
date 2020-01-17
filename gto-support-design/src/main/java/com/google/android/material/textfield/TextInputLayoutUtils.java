package com.google.android.material.textfield;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

@SuppressLint("RestrictedApi")
public final class TextInputLayoutUtils {
    private static final String TAG = "TextInputLayoutUtils";

    public static void setFocusedTextColor(@NonNull final TextInputLayout layout, @ColorInt final int color) {
        setFocusedTextColor(layout, ColorStateList.valueOf(color));
    }

    public static void setFocusedTextColor(@NonNull final TextInputLayout layout,
                                           @Nullable final ColorStateList colors) {
        layout.collapsingTextHelper.setCollapsedTextColor(colors);
        try {
            // focusedTextColor = colors;
            final Field focusedTextColor = TextInputLayout.class.getDeclaredField("focusedTextColor");
            focusedTextColor.setAccessible(true);
            focusedTextColor.set(layout, colors);
        } catch (final Exception e) {
            Timber.tag(TAG)
                    .e(e, "Error setting the focused text color on a TextInputLayout");
        }

        updateLayout(layout);
    }

    private static void updateLayout(@NonNull final TextInputLayout layout) {
        try {
            if (layout.editText != null) {
                layout.updateLabelState(false);

                // updateInputLayoutMargins();
                final Method method = TextInputLayout.class.getDeclaredMethod("updateInputLayoutMargins");
                method.setAccessible(true);
                method.invoke(layout);
            }
        } catch (@NonNull final Exception e) {
            Timber.tag(TextInputLayoutUtils.class.getSimpleName())
                    .e(e, "Error updating the layout for a TextInputLayout");
        }
    }
}
