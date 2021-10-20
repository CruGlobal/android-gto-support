package org.ccci.gto.android.common.app;

import android.content.Context;

import androidx.annotation.NonNull;

import org.ccci.gto.android.common.util.content.ContextKt;

/**
 * @deprecated Since v3.10.0, use {@link ContextKt#isApplicationDebuggable(Context)} instead.
 */
@Deprecated
public class ApplicationUtils {
    /**
     * @deprecated Since v3.10.0, use {@link ContextKt#isApplicationDebuggable(Context)} instead.
     */
    @Deprecated
    public static boolean isDebuggable(@NonNull final Context context) {
        return ContextKt.isApplicationDebuggable(context);
    }
}
