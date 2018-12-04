package org.ccci.gto.android.common.compat.util;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

public class LocaleCompat {
    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            COMPAT = new FroyoCompat();
        } else {
            COMPAT = new LollipopCompat();
        }
    }

    @NonNull
    public static Locale forLanguageTag(@NonNull final String tag) {
        return COMPAT.forLanguageTag(tag);
    }

    @NonNull
    public static String toLanguageTag(@NonNull final Locale locale) {
        return COMPAT.toLanguageTag(locale);
    }

    @VisibleForTesting
    interface Compat {
        @NonNull
        Locale forLanguageTag(@NonNull String tag);

        @NonNull
        String toLanguageTag(@NonNull Locale locale);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    static class FroyoCompat implements Compat {
        @NonNull
        @Override
        public Locale forLanguageTag(@NonNull final String tag) {
            // XXX: we are ignoring grandfathered tags unless we really need that support
            final String[] subtags = tag.split("-");
            final String language = subtags[0] != null ? subtags[0] : "";
            final String region = subtags.length > 1 && subtags[1] != null ? subtags[1] : "";
            return new Locale(language, region);
        }

        @NonNull
        @Override
        public String toLanguageTag(@NonNull final Locale locale) {
            // just perform simple generation
            final StringBuilder sb = new StringBuilder(5);

            // append the language
            sb.append(locale.getLanguage().toLowerCase(Locale.US));

            // append the region
            final String region = locale.getCountry();
            if (region != null && region.length() > 0) {
                sb.append('-').append(region.toUpperCase(Locale.US));
            }

            // output the language tag
            return sb.toString();
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static final class LollipopCompat extends FroyoCompat {
        @NonNull
        @Override
        public Locale forLanguageTag(@NonNull final String tag) {
            return Locale.forLanguageTag(tag);
        }

        @NonNull
        @Override
        public String toLanguageTag(@NonNull final Locale locale) {
            return locale.toLanguageTag();
        }
    }
}
