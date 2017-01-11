package org.ccci.gto.android.common.api.retrofit2.converter;

import android.support.annotation.Nullable;

import org.ccci.gto.android.common.util.LocaleCompat;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Converter;

class LocaleConverters {
    static final class LocaleStringConverter implements Converter<Locale, String> {
        static final LocaleStringConverter INSTANCE = new LocaleStringConverter();

        @Override
        public String convert(@Nullable final Locale locale) throws IOException {
            return locale != null ? LocaleCompat.toLanguageTag(locale) : null;
        }
    }
}
