package org.ccci.gto.android.common.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import org.ccci.gto.android.common.compat.util.LocaleCompat;

import java.util.Collections;
import java.util.HashMap;
import java.util.IllformedLocaleException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {
    static final Map<String, String> ISO3_TO_ISO2_FALLBACKS = new HashMap<>();
    static {
        ISO3_TO_ISO2_FALLBACKS.put("pse", "ms");
    }

    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            COMPAT = new FroyoCompat();
        } else {
            COMPAT = new LollipopCompat();
        }
    }

    @NonNull
    public static Locale[] getFallbacks(@NonNull final Locale locale) {
        return COMPAT.getFallbacks(locale);
    }

    @NonNull
    public static Locale[] getFallbacks(@NonNull final Locale... locales) {
        final LinkedHashSet<Locale> outputs = new LinkedHashSet<>();

        // generate fallbacks for all provided locales
        for (final Locale locale : locales) {
            Collections.addAll(outputs, getFallbacks(locale));
        }

        return outputs.toArray(new Locale[outputs.size()]);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    interface Compat {
        @NonNull
        Locale[] getFallbacks(@NonNull Locale locale);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    static class FroyoCompat implements Compat {
        @NonNull
        @Override
        public Locale[] getFallbacks(@NonNull final Locale locale) {
            // add initial locale
            final LinkedHashSet<Locale> locales = new LinkedHashSet<>();
            locales.add(locale);

            // generate all fallback variants
            String raw = LocaleCompat.toLanguageTag(locale);
            int c;
            while ((c = raw.lastIndexOf('-')) >= 0) {
                raw = raw.substring(0, c);
                locales.add(LocaleCompat.forLanguageTag(raw));
            }

            // return the locales as an array
            return locales.toArray(new Locale[locales.size()]);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static class LollipopCompat extends FroyoCompat {
        @NonNull
        @Override
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
            locales.add(builder.setRegion(null).build());
            locales.add(builder.setScript(null).build());

            // return the locales as an array
            return locales.toArray(new Locale[locales.size()]);
        }
    }
}
