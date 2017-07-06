package android.support.design.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;

public final class TabLayoutUtils {
    public static TabLayout.Tab setBackground(@NonNull final TabLayout.Tab tab, @Nullable final Drawable background) {
        ViewCompat.setBackground(tab.mView, background);
        return tab;
    }

    public static TabLayout.Tab setVisibility(@NonNull final TabLayout.Tab tab, final int visibility) {
        tab.mView.setVisibility(visibility);
        return tab;
    }
}
