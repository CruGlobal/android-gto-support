package org.ccci.gto.android.common.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.annotation.NonNull;

public class ApplicationUtils {
    /**
     * replacement for BuildConfig.DEBUG to allow libraries to check final app debug mode.
     *
     * @param context
     * @return
     */
    public static boolean isDebuggable(@NonNull final Context context) {
        return 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }
}
