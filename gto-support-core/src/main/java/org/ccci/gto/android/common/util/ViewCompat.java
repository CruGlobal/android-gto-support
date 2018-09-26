package org.ccci.gto.android.common.util;

/**
 * @deprecated Since 1.2.0.
 */
@Deprecated
public final class ViewCompat {
    /**
     * @deprecated Since 1.2.0, use {@link androidx.core.view.ViewCompat#generateViewId()} instead.
     */
    @Deprecated
    public static int generateViewId() {
        return androidx.core.view.ViewCompat.generateViewId();
    }
}
