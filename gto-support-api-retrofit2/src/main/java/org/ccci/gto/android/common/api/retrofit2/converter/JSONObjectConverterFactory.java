package org.ccci.gto.android.common.api.retrofit2.converter;

import static org.ccci.gto.android.common.retrofit2.converter.JSONObjectConverterFactory.INSTANCE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @deprecated Since v3.10.0, use {@link org.ccci.gto.android.common.retrofit2.converter.JSONObjectConverterFactory}
 * instead.
 */
@Deprecated
public final class JSONObjectConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        return INSTANCE.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return INSTANCE.responseBodyConverter(type, annotations, retrofit);
    }
}
