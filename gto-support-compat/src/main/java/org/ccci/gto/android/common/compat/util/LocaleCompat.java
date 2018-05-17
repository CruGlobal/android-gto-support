package org.ccci.gto.android.common.compat.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

import java.util.Collections;
import java.util.IllformedLocaleException;
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

    /**
     * @deprecated Since v1.2.1, use {@link org.ccci.gto.android.common.util.LocaleUtils#getFallbacks(Locale)} from
     * gto-support-util instead.
     */
    @NonNull
    @Deprecated
    public static Locale[] getFallbacks(@NonNull final Locale locale) {
        return COMPAT.getFallbacks(locale);
    }

    /**
     * @deprecated Since v1.2.1, use {@link org.ccci.gto.android.common.util.LocaleUtils#getFallbacks(Locale...)} from
     * gto-support-util instead.
     */
    @NonNull
    @Deprecated
    public static Locale[] getFallbacks(@NonNull final Locale... locales) {
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

        /**
         * @deprecated Since v1.2.1, use {@link org.ccci.gto.android.common.util.LocaleUtils} from gto-support-util
         * instead.
         */
        @NonNull
        @Deprecated
        Locale[] getFallbacks(@NonNull Locale locale);
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

        /**
         * @deprecated Since v1.2.1, use {@link org.ccci.gto.android.common.util.LocaleUtils} from gto-support-util
         * instead.
         */
        @NonNull
        @Override
        @Deprecated
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

        /**
         * @deprecated Since v1.2.1, use {@link org.ccci.gto.android.common.util.LocaleUtils} from gto-support-util
         * instead.
         */
        @NonNull
        @Override
        @Deprecated
        public Locale[] getFallbacks(@NonNull final Locale locale) {
            // add initial locale
            final LinkedHashSet<Locale> locales = new LinkedHashSet<>();
            locales.add(locale);

            // populate builder from provided locale
            final Locale.Builder builder = new Locale.Builder();
            try {
                builder.setLocale(locale).clearExtensions();
            } catch (final IllformedLocaleException e) {
                /* HACK: There appears to be a bug on Huawei devices running Android 5.0-5.1.1 using Arabic locales.
                         Setting the locale on the Locale Builder throws an IllformedLocaleException for "Invalid
                         variant: LNum". To workaround this bug we manually set the locale components on the builder,
                         skipping any invalid components
                   see: https://gist.github.com/frett/034b8eba09cf815cbcd60f83b3f52eb4
                */
                builder.clear();
                try {
                    builder.setLanguage(locale.getLanguage());
                } catch (final IllformedLocaleException ignored) {
                }
                try {
                    builder.setRegion(locale.getCountry());
                } catch (final IllformedLocaleException ignored) {
                }
                try {
                    builder.setScript(locale.getScript());
                } catch (final IllformedLocaleException ignored) {
                }
                try {
                    builder.setVariant(locale.getVariant());
                } catch (final IllformedLocaleException ignored) {
                }
            }

            // generate all fallback variants
            locales.add(builder.setVariant(null).build());
            locales.add(builder.setScript(null).build());
            locales.add(builder.setRegion(null).build());

            // return the locales as an array
            return locales.toArray(new Locale[locales.size()]);
        }
    }
}
