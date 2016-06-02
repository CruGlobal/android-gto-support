package org.ccci.gto.android.common.api.retrofit2.converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class JSONObjectConverters {
    private JSONObjectConverters() {}

    static final class JSONObjectRequestBodyConverter<T> implements Converter<T, RequestBody> {
        static final JSONObjectRequestBodyConverter<Object> INSTANCE = new JSONObjectRequestBodyConverter<>();
        private static final MediaType MEDIA_TYPE = MediaType.parse("text/json; charset=UTF-8");

        private JSONObjectRequestBodyConverter() {}

        @Override
        public RequestBody convert(T value) throws IOException {
            return RequestBody.create(MEDIA_TYPE, value != null ? value.toString() : "");
        }
    }

    static final class JSONObjectResponseBodyConverter implements Converter<ResponseBody, JSONObject> {
        static final JSONObjectResponseBodyConverter INSTANCE = new JSONObjectResponseBodyConverter();

        @Override
        public JSONObject convert(ResponseBody value) throws IOException {
            try {
                return new JSONObject(value.string());
            } catch (final JSONException e) {
                throw new IOException("Error parsing JSON", e);
            }
        }
    }

    static final class JSONArrayResponseBodyConverter implements Converter<ResponseBody, JSONArray> {
        static final JSONArrayResponseBodyConverter INSTANCE = new JSONArrayResponseBodyConverter();

        @Override
        public JSONArray convert(ResponseBody value) throws IOException {
            try {
                return new JSONArray(value.string());
            } catch (final JSONException e) {
                throw new IOException("Error parsing JSON", e);
            }
        }
    }
}
