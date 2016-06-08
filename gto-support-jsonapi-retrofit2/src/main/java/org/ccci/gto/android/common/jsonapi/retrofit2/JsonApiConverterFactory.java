package org.ccci.gto.android.common.jsonapi.retrofit2;

import android.support.annotation.NonNull;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter;
import org.ccci.gto.android.common.jsonapi.JsonApiUtils;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.json.JSONException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonApiConverterFactory extends Converter.Factory {
    @NonNull
    private final JsonApiConverter mConverter;

    private JsonApiConverterFactory(@NonNull final JsonApiConverter converter) {
        mConverter = converter;
    }

    @NonNull
    public static JsonApiConverterFactory create(@NonNull final JsonApiConverter converter) {
        return new JsonApiConverterFactory(converter);
    }

    @NonNull
    public static JsonApiConverterFactory create(@NonNull final Class<?>... classes) {
        return new JsonApiConverterFactory(new JsonApiConverter.Builder().addClasses(classes).build());
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull final Type type, final Annotation[] annotations,
                                                            final Retrofit retrofit) {
        final Class<?> c = JsonApiUtils.getRawType(type);
        if (JsonApiObject.class.isAssignableFrom(c)) {
            if (!(type instanceof ParameterizedType)) {
                throw new IllegalArgumentException("JsonApiObject needs to be parameterized");
            }

            // find the data type
            final Class<?> dataType = JsonApiUtils.getRawType(((ParameterizedType) type).getActualTypeArguments()[0]);
            return new JsonApiObjectResponseBodyConverter<>(dataType);
        } else if (List.class.isAssignableFrom(c)) {
            // TODO
        } else if (mConverter.supports(c)) {
            // TODO
        }

        return null;
    }

    private class JsonApiObjectResponseBodyConverter<T> implements Converter<ResponseBody, JsonApiObject<T>> {
        private final Class<T> mDataType;

        JsonApiObjectResponseBodyConverter(@NonNull final Class<T> type) {
            mDataType = type;
        }

        @Override
        public JsonApiObject<T> convert(final ResponseBody value) throws IOException {
            try {
                return mConverter.fromJson(value.string(), mDataType);
            } catch (final JSONException e) {
                throw new IOException("Error parsing JSON", e);
            }
        }
    }
}
