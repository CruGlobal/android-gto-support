package org.ccci.gto.android.common.api.retrofit2.converter;

import org.ccci.gto.android.common.api.retrofit2.converter.LocaleConverters.LocaleStringConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class LocaleConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == Locale.class) {
            return LocaleStringConverter.INSTANCE;
        }
        return null;
    }
}
