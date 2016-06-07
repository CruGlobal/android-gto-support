package org.ccci.gto.android.common.jsonapi;

import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiId;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodePresent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.allOf;
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

    @Test
    public void verifyToJsonSingleResourceNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter();

        final String json = converter.toJson(JsonApiObject.single(null));
        assertThat(json, jsonNodePresent("data"));
        assertThatJson(json).node("data").isEqualTo(null);
    }

    @Test
    public void verifyToJsonAttributes() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelAttributes.class);

        final String json = converter.toJson(JsonApiObject.single(new ModelAttributes()));
        assertThatJson(json).node("data").isObject();
        assertThatJson(json).node("data.attributes").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelAttributes.TYPE));
        assertThat(json, jsonPartEquals("data.attributes.attrStr1", "attrStr1"));
        assertThat(json, jsonPartEquals("data.attributes.attrInt1", 1));
        assertThat(json, jsonPartEquals("data.attributes.attrBool1", true));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn1", "attrAnn1"));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn2", "attrAnn2"));
        assertThat(json, allOf(jsonNodeAbsent("data.attributes.transientAttr"),
                               jsonNodeAbsent("data.attributes.staticAttr")));

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

    @JsonApiType(ModelAttributes.TYPE)
    public static final class ModelAttributes {
        static final String TYPE = "attrs";

        @JsonApiId
        int mId = 5;

        transient String transientAttr = "transient";
        static String staticAttr = "static";
        final String finalAttr = "final";

        private String attrStr1 = "attrStr1";
        public int attrInt1 = 1;
        boolean attrBool1 = true;

        @JsonApiAttribute
        String attrAnn1 = "attrAnn1";
        @JsonApiAttribute(name = "attrAnn2")
        String ann2 = "attrAnn2";
    }
}
