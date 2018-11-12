package com.google.android.material.tabs;

import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import timber.log.Timber;

public final class TabLayoutUtils {
    private static final String TAG = "TabLayoutUtils";
    private static Field sBaseBackgroundDrawableField;

    @Nullable
    public static Drawable getBackground(@NonNull final TabLayout.Tab tab) {
        return tab.view.getBackground();
    }

    @NonNull
    public static TabLayout.Tab setBackground(@NonNull final TabLayout.Tab tab, @Nullable final Drawable background) {
        try {
            getBaseBackgroundDrawableField().set(tab.view, background);
            tab.view.invalidate();
            tab.parent.invalidate();
        } catch (IllegalAccessException e) {
            Timber.tag(TAG)
                    .d(e, "Error setting the background for a Tab");
        } catch (NoSuchFieldException e) {
            Timber.tag(TAG)
                    .d(e, "Error setting the background for a Tab");
        }

        return tab;
    }

    @NonNull
    public static TabLayout.Tab setBackgroundTint(@NonNull final TabLayout.Tab tab, @ColorInt final int tint) {
        try {
            Drawable bkg = (Drawable) getBaseBackgroundDrawableField().get(tab.view);
            if (bkg != null) {
                bkg = DrawableCompat.wrap(bkg).mutate();
                DrawableCompat.setTint(bkg, tint);
            }
            setBackground(tab, bkg);
        } catch (IllegalAccessException e) {
            Timber.tag(TAG)
                    .d(e, "Error getting the background for a Tab");
        } catch (NoSuchFieldException e) {
            Timber.tag(TAG)
                    .d(e, "Error getting the background for a Tab");
        }

        return tab;
    }

    @NonNull
    public static TabLayout.Tab setVisibility(@NonNull final TabLayout.Tab tab, final int visibility) {
        tab.view.setVisibility(visibility);
        return tab;
    }

    private static Field getBaseBackgroundDrawableField() throws NoSuchFieldException {
        if (sBaseBackgroundDrawableField == null) {
            sBaseBackgroundDrawableField = TabLayout.TabView.class.getDeclaredField("baseBackgroundDrawable");
            sBaseBackgroundDrawableField.setAccessible(true);
        }
        return sBaseBackgroundDrawableField;
    }
}
