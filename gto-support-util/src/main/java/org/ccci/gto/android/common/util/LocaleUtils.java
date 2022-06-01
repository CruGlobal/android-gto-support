package org.ccci.gto.android.common.util;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {
    // define a few fixed fallbacks
    static final Map<String, String> FALLBACKS = new HashMap<>();
    static {
        // macrolanguage fallbacks from https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
        // Malagasy macrolanguage
        FALLBACKS.put("bhr", "mg");
        FALLBACKS.put("bjq", "mg");
        FALLBACKS.put("bmm", "mg");
        FALLBACKS.put("bzc", "mg");
        FALLBACKS.put("msh", "mg");
        FALLBACKS.put("plt", "mg");
        FALLBACKS.put("skg", "mg");
        FALLBACKS.put("tdx", "mg");
        FALLBACKS.put("tkg", "mg");
        FALLBACKS.put("txy", "mg");
        FALLBACKS.put("xmv", "mg");
        FALLBACKS.put("xmw", "mg");

        // Malay macrolanguage
        FALLBACKS.put("mfa", "ms");
        FALLBACKS.put("pse", "ms");
        FALLBACKS.put("zlm", "ms");
    }

    private static final LocaleCompat COMPAT;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            COMPAT = new LocaleCompat.Base();
        } else {
            COMPAT = new LocaleCompat.Nougat();
        }
    }

    // region Language fallback methods

    public static void addFallback(@NonNull final String locale, @NonNull final String fallback) {
        if (FALLBACKS.get(locale) != null) {
            throw new IllegalStateException(locale + " already has a fallback language defined");
        }
        FALLBACKS.put(locale, fallback);
    }

    @Nullable
    public static Locale getFallback(@NonNull final Locale locale) {
        return COMPAT.getFallback(locale);
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

        return outputs.toArray(new Locale[0]);
    }
    // endregion Language fallback methods
}
