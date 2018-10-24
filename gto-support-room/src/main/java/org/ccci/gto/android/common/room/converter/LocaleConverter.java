package org.ccci.gto.android.common.room.converter;

import org.ccci.gto.android.common.compat.util.LocaleCompat;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public final class LocaleConverter {
    @Nullable
    @TypeConverter
    public static Locale toLocale(@Nullable final String tag) {
        return tag != null ? LocaleCompat.forLanguageTag(tag) : null;
    }

    @Nullable
    @TypeConverter
    public static String toString(@Nullable final Locale locale) {
        return locale != null ? LocaleCompat.toLanguageTag(locale) : null;
    }
}
