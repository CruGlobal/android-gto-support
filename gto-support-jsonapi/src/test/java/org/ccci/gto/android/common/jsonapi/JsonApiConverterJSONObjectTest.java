package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import androidx.annotation.Nullable;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class JsonApiConverterJSONObjectTest {
    @Test
    public void verifyToJsonNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject obj = new ModelJSONObject(99);
        final String json = converter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelJSONObject.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThat(json, jsonNodeAbsent("data.attributes.object"));
        assertThat(json, jsonNodeAbsent("data.attributes.array"));
    }

    @Test
    public void verifyToJsonEmpty() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject obj = new ModelJSONObject(99);
        obj.object = new JSONObject();
        obj.array = new JSONArray();
        final String json = converter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelJSONObject.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThatJson(json).node("data.attributes.object").isObject();
        assertThatJson(json).node("data.attributes.object").isEqualTo("{}");
        assertThatJson(json).node("data.attributes.array").isArray().ofLength(0);
    }

    @Test
    public void verifyToJson() throws Exception {
        final String rawJsonObject = "{'a': 'b'}";
        final String rawJsonArray = "[1, 'pizza', " + rawJsonObject + "]";

        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject obj = new ModelJSONObject(99);
        obj.object = new JSONObject(rawJsonObject);
        obj.array = new JSONArray(rawJsonArray);
        final String json = converter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelJSONObject.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThatJson(json).node("data.attributes.object").isObject();
        assertThatJson(json).node("data.attributes.object").isEqualTo(rawJsonObject);
        assertThatJson(json).node("data.attributes.array").isArray();
        assertThatJson(json).node("data.attributes.array").isEqualTo(rawJsonArray);
    }

    @Test
    public void verifyFromJsonNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject in = new ModelJSONObject(99);
        final String json = converter.toJson(JsonApiObject.single(in));
        final ModelJSONObject out = converter.fromJson(json, ModelJSONObject.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.object, nullValue());
        assertThat(out.array, nullValue());
    }

    @Test
    public void verifyFromJsonEmpty() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject in = new ModelJSONObject(99);
        in.object = new JSONObject();
        in.array = new JSONArray();
        final String json = converter.toJson(JsonApiObject.single(in));
        final ModelJSONObject out = converter.fromJson(json, ModelJSONObject.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.object, not(nullValue()));
        assertThat(out.object.length(), is(0));
        assertThat(out.array, not(nullValue()));
        assertThat(out.array.length(), is(0));
    }

    @Test
    public void verifyFromJson() throws Exception {
        final String rawJsonObject = "{'a': 'b'}";
        final String rawJsonArray = "[1, 'pizza', " + rawJsonObject + "]";

        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject in = new ModelJSONObject(99);
        in.object = new JSONObject(rawJsonObject);
        in.array = new JSONArray(rawJsonArray);
        final String json = converter.toJson(JsonApiObject.single(in));
        final ModelJSONObject out = converter.fromJson(json, ModelJSONObject.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.array, not(nullValue()));
        assertThat(out.array.length(), is(3));
        assertThat(out.array.getInt(0), is(1));
        assertThat(out.array.getString(1), is("pizza"));
        for (final JSONObject obj : new JSONObject[] {out.object, out.array.getJSONObject(2)}) {
            assertThat(obj, not(nullValue()));
            assertThat(obj.length(), is(1));
            assertThat(obj.getString("a"), is("b"));
        }
    }

    @JsonApiType(ModelJSONObject.TYPE)
    public static class ModelJSONObject extends ModelBase {
        public static final String TYPE = "json_object";

        public ModelJSONObject() {}

        public ModelJSONObject(final int id) {
            super(id);
        }

        @Nullable
        JSONObject object;
        @Nullable
        JSONArray array;
    }
}
