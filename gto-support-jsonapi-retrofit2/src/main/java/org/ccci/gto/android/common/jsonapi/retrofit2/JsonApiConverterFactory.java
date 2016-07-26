package org.ccci.gto.android.common.jsonapi.retrofit2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter;
import org.ccci.gto.android.common.jsonapi.JsonApiUtils;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude;
import org.json.JSONException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonApiConverterFactory extends Converter.Factory {
    static final MediaType MEDIA_TYPE = MediaType.parse(JsonApiObject.MEDIA_TYPE);

    @NonNull
    final JsonApiConverter mConverter;

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

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull final Type type,
                                                          final Annotation[] parameterAnnotations,
                                                          final Annotation[] methodAnnotations,
                                                          final Retrofit retrofit) {
        // find any annotations we care about
        JsonApiInclude include = null;
        for (final Annotation ann : parameterAnnotations) {
            if (ann instanceof JsonApiInclude) {
                include = (JsonApiInclude) ann;
            }
        }

        final Class<?> c = JsonApiUtils.getRawType(type);
        if (JsonApiObject.class.isAssignableFrom(c)) {
            if (!(type instanceof ParameterizedType)) {
                throw new IllegalArgumentException("JsonApiObject needs to be parameterized");
            }

            return new JsonApiObjectRequestBodyConverter(include);
        } else if (List.class.isAssignableFrom(c)) {
            // TODO
        } else if (mConverter.supports(c)) {
            return new ObjectRequestBodyConverter(include);
        }
        return null;
    }

    @Nullable
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
            return new ObjectResponseBodyConverter<>(c);
        }

        return null;
    }

    private class JsonApiObjectRequestBodyConverter implements Converter<JsonApiObject<?>, RequestBody> {
        @Nullable
        private final String[] mInclude;

        JsonApiObjectRequestBodyConverter(@Nullable final JsonApiInclude include) {
            mInclude = include != null ? include.value() : new String[0];
        }

        @Override
        public RequestBody convert(final JsonApiObject<?> value) throws IOException {
            return RequestBody.create(MEDIA_TYPE, mConverter.toJson(value, mInclude).getBytes("UTF-8"));
        }
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

    private class ObjectRequestBodyConverter implements Converter<Object, RequestBody> {
        @NonNull
        private final JsonApiObjectRequestBodyConverter mWrappedConverter;

        ObjectRequestBodyConverter(@Nullable final JsonApiInclude include) {
            mWrappedConverter = new JsonApiObjectRequestBodyConverter(include);
        }

        @Override
        public RequestBody convert(final Object value) throws IOException {
            return mWrappedConverter.convert(JsonApiObject.single(value));
        }
    }

    private class ObjectResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final JsonApiObjectResponseBodyConverter<T> mWrappedConverter;

        ObjectResponseBodyConverter(@NonNull final Class<T> type) {
            mWrappedConverter = new JsonApiObjectResponseBodyConverter<>(type);
        }

        @Override
        public T convert(final ResponseBody value) throws IOException {
            return mWrappedConverter.convert(value).getDataSingle();
        }
    }
}
