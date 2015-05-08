package org.ccci.gto.android.common.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

/**
 * @deprecated use {@link android.support.v7.app.AlertDialog} for backwards compatible AlertDialogs
 */
@Deprecated
public class AlertDialogCompat {
    @NonNull
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static AlertDialog.Builder setView(@NonNull final AlertDialog.Builder builder,
                                              @NonNull final Context context, @LayoutRes final int layout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            builder.setView(LayoutInflater.from(context).inflate(layout, null));
        } else {
            return setView(builder, layout);
        }

        return builder;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static AlertDialog.Builder setView(@NonNull final AlertDialog.Builder builder, @LayoutRes final int layout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            throw new UnsupportedOperationException(
                    "cannot call AlertDialogCompat.setView(Builder, int) on pre-Honeycomb");
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(LayoutInflater.from(builder.getContext()).inflate(layout, null));
        } else {
            builder.setView(layout);
        }

        return builder;
    }
}
