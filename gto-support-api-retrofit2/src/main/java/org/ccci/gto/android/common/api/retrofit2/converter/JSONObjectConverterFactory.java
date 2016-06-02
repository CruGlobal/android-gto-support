package org.ccci.gto.android.common.api.retrofit2.converter;

import android.support.annotation.NonNull;

import org.ccci.gto.android.common.api.retrofit2.converter.JSONObjectConverters.JSONArrayResponseBodyConverter;
import org.ccci.gto.android.common.api.retrofit2.converter.JSONObjectConverters.JSONObjectRequestBodyConverter;
import org.ccci.gto.android.common.api.retrofit2.converter.JSONObjectConverters.JSONObjectResponseBodyConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JSONObjectConverterFactory extends Converter.Factory {
    @NonNull
    public static JSONObjectConverterFactory create() {
        return new JSONObjectConverterFactory();
    }

    private JSONObjectConverterFactory() {}

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type == JSONObject.class || type == JSONArray.class) {
            return JSONObjectRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == JSONObject.class) {
            return JSONObjectResponseBodyConverter.INSTANCE;
        }
        if (type == JSONArray.class) {
            return JSONArrayResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}
