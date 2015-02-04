package org.ccci.gto.android.common.util;

import android.text.TextUtils;

public final class StringUtils {
    private StringUtils() {
    }

    @Deprecated
    public static String join(final String sep, final String... parts) {
        return TextUtils.join(sep, parts);
    }
}
