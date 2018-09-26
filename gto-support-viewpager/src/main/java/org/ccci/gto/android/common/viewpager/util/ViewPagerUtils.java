package org.ccci.gto.android.common.viewpager.util;

import androidx.annotation.NonNull;

import org.ccci.gto.android.common.util.view.ViewUtils;

/**
 * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.view.ViewUtils} instead.
 */
@Deprecated
public class ViewPagerUtils {
    /**
     * @deprecated Since v1.2.2, use
     * {@link org.ccci.gto.android.common.util.view.ViewUtils#handleOnInterceptTouchEventException(Throwable)} instead.
     */
    @Deprecated
    public static <T extends Throwable> boolean handleOnInterceptTouchEventException(@NonNull final T cause) throws T {
        return ViewUtils.handleOnInterceptTouchEventException(cause);
    }
}
