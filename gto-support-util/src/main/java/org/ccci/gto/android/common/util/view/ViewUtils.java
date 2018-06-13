package org.ccci.gto.android.common.util.view;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;

import timber.log.Timber;

public final class ViewUtils {
    @Nullable
    public static <T> T findView(@Nullable final View root, @NonNull final Class<T> clazz, @IdRes final int id) {
        if (root != null) {
            final View view = root.findViewById(id);
            if (clazz.isInstance(view)) {
                return clazz.cast(view);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T findView(@NonNull final Activity activity, @NonNull final Class<T> clazz, @IdRes final int id) {
        final View view = activity.findViewById(id);
        if (clazz.isInstance(view)) {
            return clazz.cast(view);
        }
        return null;
    }

    @Nullable
    public static <T> T findAncestor(@NonNull final View view, @NonNull final Class<T> type) {
        ViewParent ancestor = view.getParent();
        while (ancestor != null) {
            if (type.isInstance(ancestor)) {
                return type.cast(ancestor);
            }
            ancestor = ancestor.getParent();
        }

        return null;
    }

    public static <T extends Throwable> boolean handleOnInterceptTouchEventException(@NonNull final T cause) throws T {
        if (isMotionEventPointerIndexException(cause)) {
            Timber.tag("View")
                    .d(cause, "onInterceptTouchEvent() IllegalArgumentException suppressed");
            return false;
        }
        throw cause;
    }

    public static <T extends Throwable> boolean handleOnTouchEventException(@NonNull final T cause) throws T {
        if (isMotionEventPointerIndexException(cause)) {
            Timber.tag("View")
                    .d(cause, "onTouchEvent() IllegalArgumentException suppressed");
            return true;
        }
        throw cause;
    }

    private static boolean isMotionEventPointerIndexException(@NonNull final Throwable cause) {
        return cause instanceof IllegalArgumentException && "pointerIndex out of range".equals(cause.getMessage());
    }
}
