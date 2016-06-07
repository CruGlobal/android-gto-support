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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
    public void verifySupports() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelSimple.class, ModelAttributes.class);

        assertThat(converter.supports(ModelSimple.class), is(true));
        assertThat(converter.supports(ModelAttributes.class), is(true));
        assertThat(converter.supports(Object.class), is(false));
    }

    @Test
    public void verifyToJsonSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelSimple.class);

        final ModelSimple obj0 = new ModelSimple(99);
        final String json = converter.toJson(JsonApiObject.single(obj0));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj0.mId));

        final ModelSimple obj1 = new ModelSimple(42);
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

    @Test
    public void verifyFromJsonSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelSimple.class);

        final ModelSimple source = new ModelSimple(99);
        final JsonApiObject<ModelSimple> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(source)), ModelSimple.class);
        assertThat(output.isSingle(), is(true));
        assertThat(output.getDataSingle(), is(not(nullValue())));
        assertThat(output.getDataSingle().mId, is(99));
    }

    @Test
    public void verifyFromJsonAttributes() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter(ModelAttributes.class);

        final ModelAttributes source = new ModelAttributes();
        source.mId = 19;
        source.transientAttr = "tneisnart";
        source.attrStr1 = "1rtSrtta";
        source.attrInt1 = 2;
        source.attrBool1 = false;
        source.attrAnn1 = "1nnArtta";
        source.ann2 = "2nnArtta";
        final JsonApiObject<ModelAttributes> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(source)), ModelAttributes.class);
        assertThat(output.isSingle(), is(true));
        final ModelAttributes target = output.getDataSingle();
        assertThat(target, is(not(nullValue())));
        assertThat(target.mId, is(source.mId));
        assertThat(target.transientAttr, is("transient"));
        assertThat(target.finalAttr, is("final"));
        assertThat(target.attrStr1, is(source.attrStr1));
        assertThat(target.attrInt1, is(source.attrInt1));
        assertThat(target.attrBool1, is(source.attrBool1));
        assertThat(target.attrAnn1, is(source.attrAnn1));
        assertThat(target.ann2, is(source.ann2));
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

        public ModelSimple() {}

        public ModelSimple(final int id) {
            mId = id;
        }
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
