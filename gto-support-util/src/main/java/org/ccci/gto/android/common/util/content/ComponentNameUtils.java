package org.ccci.gto.android.common.util.content;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

public class ComponentNameUtils {
    /**
     * Returns whether this is the default component for the specified Uri.
     *
     * @param manager   The PackageManager
     * @param component The component being tested
     * @param uri       The target uri
     * @return true if the target uri will resolve to the specified component
     */
    public static boolean isDefaultComponentFor(@NonNull final PackageManager manager,
                                                @NonNull final ComponentName component, @NonNull final Uri uri) {
        return component.equals(new Intent(Intent.ACTION_VIEW, uri).resolveActivity(manager));
    }

    public static boolean isDefaultComponentFor(@NonNull final Context context,
                                                @NonNull final Class<? extends Activity> clazz,
                                                @NonNull final Uri uri) {
        return isDefaultComponentFor(context.getPackageManager(), new ComponentName(context, clazz), uri);
    }
}
