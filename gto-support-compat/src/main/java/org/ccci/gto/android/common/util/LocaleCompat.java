package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;

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

    @NonNull
    public static Locale[] getFallbacks(@NonNull final Locale locale) {
        return COMPAT.getFallbacks(locale);
    }

    @NonNull
    public static Locale[] getFallbacks(final Locale... locales) {
        final LinkedHashSet<Locale> outputs = new LinkedHashSet<>();

        // generate fallbacks for all provided locales
        for (final Locale locale : locales) {
            Collections.addAll(outputs, getFallbacks(locale));
        }

        return outputs.toArray(new Locale[outputs.size()]);
    }

    @VisibleForTesting
    interface Compat {
        @NonNull
        Locale forLanguageTag(@NonNull String tag);

        @NonNull
        String toLanguageTag(@NonNull Locale locale);

        @NonNull
        Locale[] getFallbacks(@NonNull Locale locale);
    }

    @VisibleForTesting
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

        @NonNull
        @Override
        public Locale[] getFallbacks(@NonNull final Locale locale) {
            // add initial locale
            final LinkedHashSet<Locale> locales = new LinkedHashSet<>();
            locales.add(locale);

            // generate all fallback variants
            String raw = toLanguageTag(locale);
            int c;
            while ((c = raw.lastIndexOf('-')) >= 0) {
                raw = raw.substring(0, c);
                locales.add(forLanguageTag(raw));
            }

            // return the locales as an array
            return locales.toArray(new Locale[locales.size()]);
        }
    }

    @VisibleForTesting
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

        @NonNull
        @Override
        public Locale[] getFallbacks(@NonNull final Locale locale) {
            // add initial locale
            final LinkedHashSet<Locale> locales = new LinkedHashSet<>();
            locales.add(locale);

            // generate all fallback variants
            final Locale.Builder builder = new Locale.Builder();
            builder.setLocale(locale).clearExtensions();
            locales.add(builder.setVariant(null).build());
            locales.add(builder.setScript(null).build());
            locales.add(builder.setRegion(null).build());

            // return the locales as an array
            return locales.toArray(new Locale[locales.size()]);
        }
    }
}
