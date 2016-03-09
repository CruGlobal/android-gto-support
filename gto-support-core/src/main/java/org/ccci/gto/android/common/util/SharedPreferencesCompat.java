package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

public class SharedPreferencesCompat {
    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            COMPAT = new FroyoCompat();
        } else {
            COMPAT = new GingerbreadCompat();
        }
    }

    public static void apply(@NonNull final SharedPreferences.Editor prefs) {
        COMPAT.apply(prefs);
    }

    private interface Compat {
        void apply(@NonNull SharedPreferences.Editor prefs);
    }

    private static class FroyoCompat implements Compat {
        @Override
        public void apply(@NonNull final SharedPreferences.Editor prefs) {
            prefs.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static class GingerbreadCompat extends FroyoCompat {
        @Override
        public void apply(@NonNull final SharedPreferences.Editor prefs) {
            prefs.apply();
        }
    }
}
