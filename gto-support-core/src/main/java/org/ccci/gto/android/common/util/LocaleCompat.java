package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.Locale;

public class LocaleCompat {
    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String toLanguageTag(@NonNull final Locale locale) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return toLanguageTagPreLollipop(locale);
        } else {
            return locale.toLanguageTag();
        }
    }

    @NonNull
    static String toLanguageTagPreLollipop(@NonNull final Locale locale) {
        // just perform simple generation
        final StringBuilder sb = new StringBuilder(5);

        // append the language
        sb.append(locale.getLanguage().toLowerCase(Locale.US));

        // append the region
        final String region = locale.getCountry();
        if(region != null && region.length() > 0) {
            sb.append('-').append(region.toUpperCase(Locale.US));
        }

        // output the language tag
        return sb.toString();
    }
}
