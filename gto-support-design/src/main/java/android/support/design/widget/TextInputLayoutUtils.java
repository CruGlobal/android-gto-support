package android.support.design.widget;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class TextInputLayoutUtils {
    public static void setHintTextColor(@NonNull final TextInputLayout layout, @ColorInt final int color) {
        setHintTextColor(layout, ColorStateList.valueOf(color));
    }

    public static void setHintTextColor(@NonNull final TextInputLayout layout, @Nullable final ColorStateList colors) {
        setCollapsedTextColor(layout, colors);
        setExpandedTextColor(layout, colors);
    }

    public static void setExpandedTextColor(@NonNull final TextInputLayout layout, @ColorInt final int color) {
        setExpandedTextColor(layout, ColorStateList.valueOf(color));
    }

    public static void setExpandedTextColor(@NonNull final TextInputLayout layout,
                                            @Nullable final ColorStateList colors) {
        layout.mCollapsingTextHelper.setExpandedTextColor(colors);
        try {
            // mDefaultTextColor = colors;
            final Field defaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            defaultTextColor.setAccessible(true);
            defaultTextColor.set(layout, colors);
        } catch (@NonNull final Exception e) {
            Crashlytics.logException(e);
        }

        updateLayout(layout);
    }

    public static void setCollapsedTextColor(@NonNull final TextInputLayout layout, @ColorInt final int color) {
        setCollapsedTextColor(layout, ColorStateList.valueOf(color));
    }

    public static void setCollapsedTextColor(@NonNull final TextInputLayout layout,
                                             @Nullable final ColorStateList colors) {
        layout.mCollapsingTextHelper.setCollapsedTextColor(colors);
        try {
            // mFocusedTextColor = colors;
            final Field focusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            focusedTextColor.setAccessible(true);
            focusedTextColor.set(layout, colors);
        } catch (final Exception e) {
            Crashlytics.logException(e);
        }

        updateLayout(layout);
    }

    private static void updateLayout(@NonNull final TextInputLayout layout) {
        try {
            if (layout.mEditText != null) {
                layout.updateLabelState(false);

                // updateInputLayoutMargins();
                final Method method = TextInputLayout.class.getDeclaredMethod("updateInputLayoutMargins");
                method.setAccessible(true);
                method.invoke(layout);
            }
        } catch (@NonNull final Exception e) {
            Crashlytics.logException(e);
        }
    }
}
