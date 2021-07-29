package org.ccci.gto.android.common.api.retrofit2.converter;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Converter;

class LocaleConverters {
    static final class LocaleStringConverter implements Converter<Locale, String> {
        static final LocaleStringConverter INSTANCE = new LocaleStringConverter();

        @Override
        public String convert(@Nullable final Locale locale) throws IOException {
            return locale != null ? locale.toLanguageTag() : null;
        }
    }
}
