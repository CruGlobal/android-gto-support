package org.ccci.gto.android.common.util.os;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.ccci.gto.android.common.compat.util.LocaleCompat;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Array;
import java.util.Locale;

public class BundleUtils {
    @Nullable
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <T extends Parcelable> T[] getParcelableArray(@NonNull final Bundle bundle,
                                                                @Nullable final String key,
                                                                @NonNull final Class<T> clazz) {
        final Parcelable[] raw = bundle.getParcelableArray(key);
        if (raw == null) {
            return null;
        }

        // copy all objects to typed array
        final T[] arr = (T[]) Array.newInstance(clazz, raw.length);
        System.arraycopy(raw, 0, arr, 0, raw.length);
        return arr;
    }

    public static void putEnum(@NonNull final Bundle bundle, @Nullable final String key,
                               @Nullable final Enum<?> value) {
        bundle.putString(key, value != null ? value.name() : null);
    }

    @Nullable
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key) {
        return getEnum(bundle, type, key, null);
    }

    @Nullable
    @Contract("_, _, _, !null -> !null")
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key, @Nullable final T defValue) {
        final String raw = bundle.getString(key);
        if (raw == null) {
            return defValue;
        }

        try {
            return Enum.valueOf(type, raw);
        } catch (final IllegalArgumentException e) {
            return defValue;
        }
    }

    public static void putLocale(@NonNull final Bundle bundle, @Nullable final String key,
                                 @Nullable final Locale locale) {
        bundle.putString(key, locale != null ? LocaleCompat.toLanguageTag(locale) : null);
    }

    @Nullable
    public static Locale getLocale(@NonNull final Bundle bundle, @Nullable final String key) {
        return getLocale(bundle, key, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static Locale getLocale(@NonNull final Bundle bundle, @Nullable final String key,
                                   @Nullable final Locale defValue) {
        final String raw = bundle.getString(key);
        if (raw == null) {
            return defValue;
        }
        return LocaleCompat.forLanguageTag(raw);
    }

    public static void putLocaleArray(@NonNull final Bundle bundle, @Nullable final String key,
                                      @Nullable final Locale[] locales) {
        putLocaleArray(bundle, key, locales, false);
    }

    /**
     * Store an array of Locales in the provided Bundle
     *
     * @param bundle       The bundle to store the locale array in
     * @param key          The key to store the locale array under
     * @param locales      The locales being put in the bundle
     * @param singleString Flag indicating if the locale array should be stored as a single string
     */
    public static void putLocaleArray(@NonNull final Bundle bundle, @Nullable final String key,
                                      @Nullable final Locale[] locales, final boolean singleString) {
        final String[] array;
        if (locales != null) {
            array = new String[locales.length];
            for (int i = 0; i < locales.length; i++) {
                array[i] = locales[i] != null ? LocaleCompat.toLanguageTag(locales[i]) : null;
            }
        } else {
            array = null;
        }

        if (singleString) {
            bundle.putString(key, array != null ? TextUtils.join(",", array) : null);
        } else {
            bundle.putStringArray(key, array);
        }
    }

    @Nullable
    public static Locale[] getLocaleArray(@NonNull final Bundle bundle, @Nullable final String key) {
        String[] raw = bundle.getStringArray(key);
        if (raw == null) {
            final String flat = bundle.getString(key);
            raw = flat != null ? TextUtils.split(flat, ",") : null;
        }

        final Locale[] locales;
        if (raw != null) {
            locales = new Locale[raw.length];
            for (int i = 0; i < raw.length; i++) {
                locales[i] = raw[i] != null ? LocaleCompat.forLanguageTag(raw[i]) : null;
            }
        } else {
            locales = null;
        }
        return locales;
    }
}
