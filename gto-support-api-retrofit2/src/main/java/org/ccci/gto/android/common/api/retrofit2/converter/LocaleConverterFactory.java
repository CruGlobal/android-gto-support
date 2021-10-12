package org.ccci.gto.android.common.api.retrofit2.converter;

import static org.ccci.gto.android.common.retrofit2.converter.LocaleConverterFactory.INSTANCE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @deprecated Since v3.10.0, use {@link org.ccci.gto.android.common.retrofit2.converter.LocaleConverterFactory} instead
 */
@Deprecated
public class LocaleConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return INSTANCE.stringConverter(type, annotations, retrofit);
    }
}
