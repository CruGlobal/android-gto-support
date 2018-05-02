package org.ccci.gto.android.common.viewpager.util;

import android.support.annotation.NonNull;

import timber.log.Timber;

public class ViewPagerUtils {
    public static <T extends Throwable> boolean handleOnInterceptTouchEventException(@NonNull final T cause) throws T {
        if (cause instanceof IllegalArgumentException) {
            Timber.tag("ViewPager")
                    .d(cause, "onInterceptTouchEvent() IllegalArgumentException suppressed");
            return false;
        }
        throw cause;
    }
}
