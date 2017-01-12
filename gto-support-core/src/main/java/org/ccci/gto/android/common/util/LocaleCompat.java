package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * @deprecated Since v1.0.3, LocaleCompat was moved to a compat package. use it directly from there.
 */
@Deprecated
public class LocaleCompat {
    @NonNull
    public static Locale forLanguageTag(@NonNull final String tag) {
        return org.ccci.gto.android.common.compat.util.LocaleCompat.forLanguageTag(tag);
    }

    @NonNull
    public static String toLanguageTag(@NonNull final Locale locale) {
        return org.ccci.gto.android.common.compat.util.LocaleCompat.toLanguageTag(locale);
    }

    @NonNull
    public static Locale[] getFallbacks(@NonNull final Locale locale) {
        return org.ccci.gto.android.common.compat.util.LocaleCompat.getFallbacks(locale);
    }

    @NonNull
    public static Locale[] getFallbacks(final Locale... locales) {
        return org.ccci.gto.android.common.compat.util.LocaleCompat.getFallbacks(locales);
    }
}
