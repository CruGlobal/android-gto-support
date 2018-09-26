package org.ccci.gto.android.common.jsonapi.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.compat.util.LocaleCompat;

import java.util.Locale;

public class LocaleTypeConverter implements TypeConverter<Locale> {
    @Override
    public boolean supports(@NonNull final Class<?> clazz) {
        return Locale.class.equals(clazz);
    }

    @Nullable
    @Override
    public String toString(@Nullable final Locale locale) {
        return locale != null ? LocaleCompat.toLanguageTag(locale) : null;
    }

    @Nullable
    @Override
    public Locale fromString(@Nullable final String langTag) {
        return langTag != null ? LocaleCompat.forLanguageTag(langTag) : null;
    }
}
