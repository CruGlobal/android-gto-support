package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.Locale;

public class LocaleCompat {
    @NonNull
    public static Locale forLanguageTag(@NonNull final String tag) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return FroyoLocale.forLanguageTag(tag);
        } else {
            return LollipopLocale.forLanguageTag(tag);
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String toLanguageTag(@NonNull final Locale locale) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return FroyoLocale.toLanguageTag(locale);
        } else {
            return LollipopLocale.toLanguageTag(locale);
        }
    }

    @VisibleForTesting
    static final class FroyoLocale {
        @NonNull
        static Locale forLanguageTag(@NonNull final String tag) {
            // XXX: we are ignoring grandfathered tags unless we really need that support
            final String[] subtags = tag.split("-");
            final String language = subtags[0] != null ? subtags[0] : "";
            final String region = subtags.length > 1 && subtags[1] != null ? subtags[1] : "";
            return new Locale(language, region);
        }

        @NonNull
        static String toLanguageTag(@NonNull final Locale locale) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static final class LollipopLocale {
        @NonNull
        static Locale forLanguageTag(@NonNull final String tag) {
            return Locale.forLanguageTag(tag);
        }

        @NonNull
        static String toLanguageTag(@NonNull final Locale locale) {
            return locale.toLanguageTag();
        }
    }
}
