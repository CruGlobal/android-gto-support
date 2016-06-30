package org.ccci.gto.android.common.jsonapi.retrofit2;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JsonApiConverterFactoryTest {
    private static final String SIMPLE_SINGLE_RAW_JSON = "{data:{id:5,type:\"simple\"}}";

    @JsonApiType(ModelSimple.TYPE)
    public static class ModelSimple {
        static final String TYPE = "simple";

        @JsonApiId
        int mId;

        String attr1;

        public ModelSimple() {}

        public ModelSimple(final int id, final String attr) {
            mId = id;
            attr1 = attr;
        }
    }

    interface Service {
        @POST("/")
        Call<JsonApiObject<ModelSimple>> post(@Body JsonApiObject<ModelSimple> model);

        @POST("/")
        Call<ModelSimple> post(@Body ModelSimple model);
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private Service service;

    @Before
    public void setUp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(JsonApiConverterFactory.create(ModelSimple.class))
                .build();
        service = retrofit.create(Service.class);
    }

    @Test
    public void verifyWrappedObj() throws Exception {
        server.enqueue(new MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON));

        final Call<JsonApiObject<ModelSimple>> call = service.post(JsonApiObject.single(new ModelSimple(42, "blah")));
        final Response<JsonApiObject<ModelSimple>> response = call.execute();
        final JsonApiObject<ModelSimple> body = response.body();
        assertNotNull(body);
        assertThat(body.isSingle(), is(true));
        final ModelSimple obj = body.getDataSingle();
        assertNotNull(obj);
        assertThat(obj.mId, is(5));

        final RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("Content-Type"), is(JsonApiObject.MEDIA_TYPE));
        final String json = request.getBody().readUtf8();
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE));
        assertThat(json, jsonPartEquals("data.id", 42));
        assertThat(json, jsonPartEquals("data.attributes.attr1", "blah"));
    }

    @Test
    public void verifyPlainObj() throws Exception {
        server.enqueue(new MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON));

        final Call<ModelSimple> call = service.post(new ModelSimple(42, "blah"));
        final Response<ModelSimple> response = call.execute();
        final ModelSimple obj = response.body();
        assertNotNull(obj);
        assertThat(obj.mId, is(5));

        final RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("Content-Type"), is(JsonApiObject.MEDIA_TYPE));
        final String json = request.getBody().readUtf8();
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE));
        assertThat(json, jsonPartEquals("data.id", 42));
        assertThat(json, jsonPartEquals("data.attributes.attr1", "blah"));
    }
}
