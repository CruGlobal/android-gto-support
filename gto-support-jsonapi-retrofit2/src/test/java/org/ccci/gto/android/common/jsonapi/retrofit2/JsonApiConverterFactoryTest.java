package org.ccci.gto.android.common.jsonapi.retrofit2;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.retrofit2.annotation.JsonApiInclude;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_FIELDS;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JsonApiConverterFactoryTest {
    private static final String SIMPLE_SINGLE_RAW_JSON = "{data:{id:5,type:\"simple\"}}";

    private abstract static class ModelBase {
        @JsonApiId
        int mId;
    }

    @JsonApiType(ModelSimple.TYPE)
    public static class ModelSimple extends ModelBase {
        static final String TYPE = "simple";

        String attr1;

        public ModelSimple() {}

        public ModelSimple(final int id, final String attr) {
            mId = id;
            attr1 = attr;
        }
    }

    @JsonApiType(ModelParent.TYPE)
    public static class ModelParent extends ModelBase {
        static final String TYPE = "parent";

        List<ModelChild> children = new ArrayList<>();
        ModelChild favorite;
    }

    @JsonApiType(ModelChild.TYPE)
    public static class ModelChild extends ModelBase {
        static final String TYPE = "child";

        String name;

        public ModelChild(final String name) {
            this.name = name;
        }
    }

    interface Service {
        @POST("/")
        Call<JsonApiObject<ModelSimple>> post(@Body JsonApiObject<ModelSimple> model);

        @POST("/")
        Call<ModelSimple> post(@Body ModelSimple model);

        @POST("/")
        Call<ModelSimple> postInclude(@Body @JsonApiInclude("favorite") ModelParent model);
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private Service service;

    @Before
    public void setUp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(
                        JsonApiConverterFactory.create(ModelSimple.class, ModelParent.class, ModelChild.class))
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
        assertThat(json, jsonNodeAbsent("included"));
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
        assertThat(json, jsonNodeAbsent("included"));
    }

    @Test
    public void verifyPostIncludes() throws Exception {
        server.enqueue(new MockResponse().setBody(SIMPLE_SINGLE_RAW_JSON));

        final ModelParent parent = new ModelParent();
        parent.mId = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.mId = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You");
        child2.mId = 20;
        parent.children.add(child2);

        final Call<ModelSimple> call = service.postInclude(parent);
        final Response<ModelSimple> response = call.execute();
        final ModelSimple obj = response.body();
        assertNotNull(obj);
        assertThat(obj.mId, is(5));

        final RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("Content-Type"), is(JsonApiObject.MEDIA_TYPE));
        final String json = request.getBody().readUtf8();
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThat(json, jsonPartEquals("data.id", 1));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.type", ModelChild.TYPE));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.id", parent.favorite.mId));
        assertThat(json, jsonNodeAbsent("data.relationships.favorite.data.attributes"));
        assertThatJson(json).node("data.relationships.children.data").isArray().ofLength(2);
        assertThatJson(json).node("included").isArray().ofLength(1);
        assertThatJson(json).node("included").matches(
                hasItem(jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}").when(IGNORING_EXTRA_FIELDS)));
        assertThatJson(json).node("included").matches(not(hasItem(
                jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}").when(IGNORING_EXTRA_FIELDS))));
    }
}
