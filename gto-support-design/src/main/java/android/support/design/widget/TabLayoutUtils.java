package android.support.design.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;

public final class TabLayoutUtils {
    @Nullable
    public static Drawable getBackground(@NonNull final TabLayout.Tab tab) {
        return tab.mView.getBackground();
    }

    @NonNull
    public static TabLayout.Tab setBackground(@NonNull final TabLayout.Tab tab, @Nullable final Drawable background) {
        ViewCompat.setBackground(tab.mView, background);
        return tab;
    }

    @NonNull
    public static TabLayout.Tab setBackgroundTint(@NonNull final TabLayout.Tab tab, @ColorInt final int tint) {
        Drawable bkg = tab.mView.getBackground();
        if (bkg != null) {
            bkg = DrawableCompat.wrap(bkg).mutate();
            DrawableCompat.setTint(bkg, tint);
        }
        setBackground(tab, bkg);
        return tab;
    }

    @NonNull
    public static TabLayout.Tab setVisibility(@NonNull final TabLayout.Tab tab, final int visibility) {
        tab.mView.setVisibility(visibility);
        return tab;
    }
}
