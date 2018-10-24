package com.google.android.material.tabs;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

public final class TabLayoutUtils {
    @Nullable
    public static Drawable getBackground(@NonNull final TabLayout.Tab tab) {
        return tab.view.getBackground();
    }

    @NonNull
    public static TabLayout.Tab setBackground(@NonNull final TabLayout.Tab tab, @Nullable final Drawable background) {
        ViewCompat.setBackground(tab.view, background);
        return tab;
    }

    @NonNull
    public static TabLayout.Tab setBackgroundTint(@NonNull final TabLayout.Tab tab, @ColorInt final int tint) {
        Drawable bkg = tab.view.getBackground();
        if (bkg != null) {
            bkg = DrawableCompat.wrap(bkg).mutate();
            DrawableCompat.setTint(bkg, tint);
        }
        setBackground(tab, bkg);
        return tab;
    }

    @NonNull
    public static TabLayout.Tab setVisibility(@NonNull final TabLayout.Tab tab, final int visibility) {
        tab.view.setVisibility(visibility);
        return tab;
    }
}
