package org.ccci.gto.android.common.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.Locale;

/**
 * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils} instead.
 */
@Deprecated
public class BundleUtils {
    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getParcelableArray(Bundle,
     * String, Class)} instead.
     */
    @Nullable
    @Deprecated
    public static <T extends Parcelable> T[] getParcelableArray(@NonNull final Bundle bundle,
                                                                @Nullable final String key,
                                                                @NonNull final Class<T> clazz) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getParcelableArray(bundle, key, clazz);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#putEnum(Bundle, String,
     * Enum)} instead.
     */
    @Deprecated
    public static void putEnum(@NonNull final Bundle bundle, @Nullable final String key,
                               @Nullable final Enum<?> value) {
        org.ccci.gto.android.common.util.os.BundleUtils.putEnum(bundle, key, value);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getEnum(Bundle, Class,
     * String)} instead.
     */
    @Nullable
    @Deprecated
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getEnum(bundle, type, key);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getEnum(Bundle, Class,
     * String, Enum)} instead.
     */
    @Nullable
    @Deprecated
    @Contract("_, _, _, !null -> !null")
    public static <T extends Enum<T>> T getEnum(@NonNull final Bundle bundle, @NonNull final Class<T> type,
                                                @Nullable final String key, @Nullable final T defValue) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getEnum(bundle, type, key, defValue);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#putLocale(Bundle, String,
     * Locale)} instead.
     */
    @Deprecated
    public static void putLocale(@NonNull final Bundle bundle, @Nullable final String key,
                                 @Nullable final Locale locale) {
        org.ccci.gto.android.common.util.os.BundleUtils.putLocale(bundle, key, locale);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getLocale(Bundle, String)}
     * instead.
     */
    @Nullable
    @Deprecated
    public static Locale getLocale(@NonNull final Bundle bundle, @Nullable final String key) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getLocale(bundle, key);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getLocale(Bundle, String,
     * Locale)} instead.
     */
    @Nullable
    @Deprecated
    @Contract("_, _, !null -> !null")
    public static Locale getLocale(@NonNull final Bundle bundle, @Nullable final String key,
                                   @Nullable final Locale defValue) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getLocale(bundle, key, defValue);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#putLocaleArray(Bundle,
     * String, Locale[])} instead.
     */
    @Deprecated
    public static void putLocaleArray(@NonNull final Bundle bundle, @Nullable final String key,
                                      @Nullable final Locale[] locales) {
        org.ccci.gto.android.common.util.os.BundleUtils.putLocaleArray(bundle, key, locales);
    }

    /**
     * @deprecated Since v1.2.2, use {@link org.ccci.gto.android.common.util.os.BundleUtils#getLocaleArray(Bundle,
     * String)} instead.
     */
    @Nullable
    @Deprecated
    public static Locale[] getLocaleArray(@NonNull final Bundle bundle, @Nullable final String key) {
        return org.ccci.gto.android.common.util.os.BundleUtils.getLocaleArray(bundle, key);
    }
}
