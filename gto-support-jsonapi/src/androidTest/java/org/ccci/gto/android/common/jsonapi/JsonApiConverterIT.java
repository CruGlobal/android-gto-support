package org.ccci.gto.android.common.jsonapi;

import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class JsonApiConverterIT {
    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterNoType() throws Exception {
        new JsonApiConverter(ModelNoType.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterDuplicateTypes() throws Exception {
        new JsonApiConverter(ModelDuplicateType1.class, ModelDuplicateType2.class);
    }

    @Test
    public void verifyToJsonSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelSimple.class);

        final ModelSimple obj0 = new ModelSimple();
        obj0.mId = 99;
        final String json = converter.toJson(JsonApiObject.single(obj0));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj0.mId));

        final ModelSimple obj1 = new ModelSimple();
        obj1.mId = 42;
        final String json2 = converter.toJson(JsonApiObject.of(obj0, obj1));
        assertThatJson(json2).node("data").isArray();
        assertThat(json2, jsonPartEquals("data[0].type", ModelSimple.TYPE));
        assertThat(json2, jsonPartEquals("data[0].id", obj0.mId));
        assertThat(json2, jsonPartEquals("data[1].type", ModelSimple.TYPE));
        assertThat(json2, jsonPartEquals("data[1].id", obj1.mId));
    }

    public static final class ModelNoType {}

    @JsonApiType("type")
    public static final class ModelDuplicateType1 {}

    @JsonApiType("type")
    public static final class ModelDuplicateType2 {}

    @JsonApiType(ModelSimple.TYPE)
    public static final class ModelSimple {
        static final String TYPE = "simple";

        @JsonApiId
        int mId;
    }
}
