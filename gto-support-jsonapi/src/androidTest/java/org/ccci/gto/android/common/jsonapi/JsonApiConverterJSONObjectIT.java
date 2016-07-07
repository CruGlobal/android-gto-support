package org.ccci.gto.android.common.jsonapi;

import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class JsonApiConverterJSONObjectIT {
    @Test
    public void verifyToJsonNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject obj = new ModelJSONObject(99);
        final String json = converter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelJSONObject.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.mId));
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
        assertThat(json, jsonPartEquals("data.id", obj.mId));
        assertThatJson(json).node("data.attributes.object").isObject();
        assertThatJson(json).node("data.attributes.object").isEqualTo("{}");
        assertThatJson(json).node("data.attributes.array").isArray().ofLength(0);
    }

    @Test
    public void verifyToJson() throws Exception {
        final String JSON_OBJECT = "{'a': 'b'}";
        final String JSON_ARRAY = "[1, 'pizza', " + JSON_OBJECT + "]";

        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject obj = new ModelJSONObject(99);
        obj.object = new JSONObject(JSON_OBJECT);
        obj.array = new JSONArray(JSON_ARRAY);
        final String json = converter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelJSONObject.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.mId));
        assertThatJson(json).node("data.attributes.object").isObject();
        assertThatJson(json).node("data.attributes.object").isEqualTo(JSON_OBJECT);
        assertThatJson(json).node("data.attributes.array").isArray();
        assertThatJson(json).node("data.attributes.array").isEqualTo(JSON_ARRAY);
    }

    @Test
    public void verifyFromJsonNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject in = new ModelJSONObject(99);
        final String json = converter.toJson(JsonApiObject.single(in));
        final ModelJSONObject out = converter.fromJson(json, ModelJSONObject.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.mId, is(in.mId));
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
        assertThat(out.mId, is(in.mId));
        assertThat(out.object, not(nullValue()));
        assertThat(out.object.length(), is(0));
        assertThat(out.array, not(nullValue()));
        assertThat(out.array.length(), is(0));
    }

    @Test
    public void verifyFromJson() throws Exception {
        final String JSON_OBJECT = "{'a': 'b'}";
        final String JSON_ARRAY = "[1, 'pizza', " + JSON_OBJECT + "]";

        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelJSONObject.class).build();

        final ModelJSONObject in = new ModelJSONObject(99);
        in.object = new JSONObject(JSON_OBJECT);
        in.array = new JSONArray(JSON_ARRAY);
        final String json = converter.toJson(JsonApiObject.single(in));
        final ModelJSONObject out = converter.fromJson(json, ModelJSONObject.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.mId, is(in.mId));
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
            mId = id;
        }

        @Nullable
        JSONObject object;
        @Nullable
        JSONArray array;
    }
}
