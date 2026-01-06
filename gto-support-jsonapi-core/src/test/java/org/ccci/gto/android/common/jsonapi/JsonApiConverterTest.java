package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiAttribute;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.ccci.gto.android.common.jsonapi.model.ModelSimple;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Collections;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodePresent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartMatches;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class JsonApiConverterTest {
    private static final float DELTA = 0.000001f;

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterNoType() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelNoType.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterDuplicateTypes() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelDuplicateType1.class, ModelDuplicateType2.class).build();
    }

    @Test
    public void verifySupports() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelSimple.class, ModelAttributes.class).build();

        assertThat(converter.supports(ModelSimple.class), is(true));
        assertThat(converter.supports(ModelAttributes.class), is(true));
        assertThat(converter.supports(Object.class), is(false));
    }

    @Test
    public void verifyToJsonSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        final ModelSimple obj0 = new ModelSimple(99);
        final String json = converter.toJson(JsonApiObject.single(obj0));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelSimple.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj0.id));

        final ModelSimple obj1 = new ModelSimple(42);
        final String json2 = converter.toJson(JsonApiObject.of(obj0, obj1));
        assertThatJson(json2).node("data").isArray();
        assertThat(json2, jsonPartEquals("data[0].type", ModelSimple.TYPE));
        assertThat(json2, jsonPartEquals("data[0].id", obj0.id));
        assertThat(json2, jsonPartEquals("data[1].type", ModelSimple.TYPE));
        assertThat(json2, jsonPartEquals("data[1].id", obj1.id));
    }

    @Test
    public void verifyToJsonSingleResourceNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().build();

        final String json = converter.toJson(JsonApiObject.single(null));
        assertThat(json, jsonNodePresent("data"));
        assertThatJson(json).node("data").isEqualTo(null);
    }

    @Test
    public void verifyToJsonAttributes() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelAttributes.class).build();

        final String json = converter.toJson(JsonApiObject.single(new ModelAttributes()));
        assertThatJson(json).node("data").isObject();
        assertThatJson(json).node("data.attributes").isObject();
        assertThatJson(json).node("data.attributes.attrArrayDouble").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayFloat").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayFloatBoxed").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayInt").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayLong").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayBoolean").isArray().hasSize(2);
        assertThatJson(json).node("data.attributes.attrArrayString").isArray().hasSize(3);
        assertThat(json, jsonPartEquals("data.type", ModelAttributes.TYPE));
        assertThat(json, jsonPartEquals("data.attributes.attrStr1", "attrStr1"));
        assertThat(json, jsonPartEquals("data.attributes.attrInt1", 1));
        assertThat(json, jsonPartEquals("data.attributes.attrFloat", 1.5f));
        assertThat(json, jsonPartEquals("data.attributes.attrFloatBoxed", 2.5f));
        assertThat(json, jsonPartEquals("data.attributes.attrBool1", true));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn1", "attrAnn1"));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn2", "attrAnn2"));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn3", "attrAnn3"));
        assertThat(json, jsonPartEquals("data.attributes.attrAnn4", "attrAnn4"));
        assertThat(json, jsonNodeAbsent("data.attributes.ignoredName"));
        assertThat(json, allOf(jsonNodeAbsent("data.attributes.transientAttr"),
                               jsonNodeAbsent("data.attributes.staticAttr")));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayDouble[0]", 1.5));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayDouble[1]", 2.5));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayFloat[0]", 1.25f));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayFloat[1]", 2.75f));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayFloatBoxed[0]", 3.25f));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayFloatBoxed[1]", 4.75f));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayLong[0]", 1));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayLong[1]", 2));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayInt[0]", 3));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayInt[1]", 4));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayBoolean[0]", true));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayBoolean[1]", false));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayString[0]", "a"));
        assertThat(json, jsonPartMatches("data.attributes.attrArrayString[1]", nullValue()));
        assertThat(json, jsonPartEquals("data.attributes.attrArrayString[2]", "b"));
    }

    @Test
    public void verifyToJsonMetaNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().build();

        final JsonApiObject<?> obj = JsonApiObject.of();
        obj.setRawMeta(null);
        final String json = converter.toJson(obj);
        assertThat(json, jsonNodeAbsent("meta"));
    }

    @Test
    public void verifyToJsonMeta() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().build();

        final JsonApiObject<?> obj = JsonApiObject.of();
        obj.setRawMeta(new JSONObject(Collections.singletonMap("attr", "value")));
        final String json = converter.toJson(obj);
        assertThat(json, jsonNodePresent("meta"));
        assertThat(json, jsonPartEquals("meta.attr", "value"));
    }

    @Test
    public void verifyFromJsonSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        final ModelSimple source = new ModelSimple(99);
        final JsonApiObject<ModelSimple> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(source)), ModelSimple.class);
        assertThat(output.isSingle(), is(true));
        assertThat(output.getDataSingle(), is(not(nullValue())));
        assertThat(output.getDataSingle().id, is(99));
    }

    @Test
    public void verifyFromJsonSimpleAlias() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        // valid types
        for (final String type : new String[] {ModelSimple.TYPE, ModelSimple.ALIAS1, ModelSimple.ALIAS2}) {
            final JsonApiObject<ModelSimple> output =
                    converter.fromJson("{data:{type:'" + type + "',id:79}}", ModelSimple.class);
            final ModelSimple obj = output.getDataSingle();
            assertNotNull(obj);
            assertThat(obj.id, is(79));
        }

        // invalid types
        for (final String type : new String[] {ModelSimple.NOTALIAS}) {
            final JsonApiObject<ModelSimple> output =
                    converter.fromJson("{data:{type:'" + type + "',id:79}}", ModelSimple.class);
            assertNull(output.getDataSingle());
        }
    }

    @Test
    public void verifyFromJsonAttributes() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelAttributes.class).build();

        final ModelAttributes source = new ModelAttributes();
        source.id = 19;
        source.transientAttr = "tneisnart";
        source.attrStr1 = "1rtSrtta";
        source.attrFloat = 3.4f;
        source.attrFloatBoxed = null;
        source.attrInt1 = 2;
        source.attrIntegerBoxed = 3;
        source.attrBool1 = false;
        source.attrAnn1 = "1nnArtta";
        source.ann2 = "2nnArtta";
        source.ann3 = "3nnArtta";
        source.ann4 = "4nnArtta";
        source.attrArrayBoolean = new boolean[] {false};
        source.attrArrayDouble = new double[] {2.73, 3.14};
        source.attrArrayFloat = new float[] {1.34f, -2.19f};
        source.attrArrayFloatBoxed = new Float[] {1.34f, null, -2.19f};
        source.attrArrayInt = new int[] {11, 12, 13};
        source.attrArrayLong = new long[] {21, 22};
        source.attrArrayString = new String[] {null, "str1", "str2"};

        final String json = converter.toJson(
                JsonApiObject.single(source),
                JsonApiConverter.Options.builder().serializeNullAttributes(ModelAttributes.TYPE).build()
        );
        final JsonApiObject<ModelAttributes> output = converter.fromJson(json, ModelAttributes.class);
        assertThat(output.isSingle(), is(true));
        final ModelAttributes target = output.getDataSingle();
        assertThat(target, is(not(nullValue())));
        assertThat(target.id, is(source.id));
        assertThat(target.transientAttr, is("transient"));
        assertThat(target.finalAttr, is("final"));
        assertThat(target.attrStr1, is(source.attrStr1));
        assertEquals(source.attrFloat, target.attrFloat, DELTA);
        assertNull(target.attrFloatBoxed);
        assertThat(target.attrInt1, is(source.attrInt1));
        assertEquals(source.attrIntegerBoxed, target.attrIntegerBoxed);
        assertThat(target.attrBool1, is(source.attrBool1));
        assertThat(target.attrAnn1, is(source.attrAnn1));
        assertThat(target.ann2, is(source.ann2));
        assertThat(target.ann3, is(source.ann3));
        assertThat(target.ann4, is(source.ann4));
        assertArrayEquals(source.attrArrayBoolean, target.attrArrayBoolean);
        assertArrayEquals(source.attrArrayDouble, target.attrArrayDouble, DELTA);
        assertArrayEquals(source.attrArrayFloat, target.attrArrayFloat, DELTA);
        assertArrayEquals(source.attrArrayFloatBoxed, target.attrArrayFloatBoxed);
        assertArrayEquals(source.attrArrayInt, target.attrArrayInt);
        assertArrayEquals(source.attrArrayLong, target.attrArrayLong);
        assertArrayEquals(source.attrArrayString, target.attrArrayString);
    }

    @Test
    public void verifyFromJsonMetaNull() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().build();

        final JsonApiObject<?> obj = JsonApiObject.of();
        obj.setRawMeta(null);
        final JsonApiObject<?> output = converter.fromJson(converter.toJson(obj), Object.class);
        assertNull(output.getRawMeta());
    }

    @Test
    public void verifyFromJsonMeta() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().build();

        final JsonApiObject<?> obj = JsonApiObject.of();
        obj.setRawMeta(new JSONObject(Collections.singletonMap("attr", "value")));
        final JsonApiObject<?> output = converter.fromJson(converter.toJson(obj), Object.class);
        assertNotNull(output.getRawMeta());
        assertThatJson(output.getRawMeta().toString()).isEqualTo(obj.getRawMeta().toString());
    }

    // region Serialize flags
    @Test
    public void verifyToJsonSerializeFlags() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelAttributes.class).build();

        final String json = converter.toJson(JsonApiObject.single(new ModelAttributes()));
        assertThatJson(json).node("data").isObject();
        assertThatJson(json).node("data.attributes").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelAttributes.TYPE));
        assertThat(json, jsonPartEquals("data.attributes.attrSerializeOnly", "serialize"));
        assertThat(json, jsonNodeAbsent("data.attributes.attrDeserializeOnly"));
    }

    @Test
    public void verifyFromJsonSerializeFlags() throws Exception {
        final String raw = "{data:{id:5,type:'attrs',attributes:{attrSerializeOnly:'a',attrDeserializeOnly:'b'}}}";

        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelAttributes.class).build();
        final JsonApiObject<ModelAttributes> output =
                converter.fromJson(raw, ModelAttributes.class);
        assertThat(output.isSingle(), is(true));
        final ModelAttributes target = output.getDataSingle();
        assertThat(target, is(not(nullValue())));
        assertEquals("serialize", target.attrSerializeOnly);
        assertEquals("b", target.attrDeserializeOnly);
    }
    // endregion Serialize flags

    public static final class ModelNoType {}

    @JsonApiType("type")
    public static final class ModelDuplicateType1 {}

    @JsonApiType("type")
    public static final class ModelDuplicateType2 {}

    @JsonApiType(ModelAttributes.TYPE)
    public static final class ModelAttributes extends ModelBase {
        static final String TYPE = "attrs";

        transient String transientAttr = "transient";
        static String staticAttr = "static";
        final String finalAttr = "final";

        private String attrStr1 = "attrStr1";
        float attrFloat = 1.5f;
        Float attrFloatBoxed = 2.5f;
        public int attrInt1 = 1;
        Integer attrIntegerBoxed = 1;
        boolean attrBool1 = true;

        private double[] attrArrayDouble = {1.5, 2.5};
        private float[] attrArrayFloat = {1.25f, 2.75f};
        private Float[] attrArrayFloatBoxed = {3.25f, 4.75f};
        private long[] attrArrayLong = {1, 2};
        private int[] attrArrayInt = {3, 4};
        private boolean[] attrArrayBoolean = {true, false};
        private String[] attrArrayString = {"a", null, "b"};

        // attribute naming test
        @JsonApiAttribute
        String attrAnn1 = "attrAnn1";
        @JsonApiAttribute(name = "attrAnn2")
        String ann2 = "attrAnn2";
        @JsonApiAttribute("attrAnn3")
        String ann3 = "attrAnn3";
        @JsonApiAttribute(name = "attrAnn4", value = "ignoredName")
        String ann4 = "attrAnn4";

        // serialize & deserialize flags test
        @JsonApiAttribute(deserialize = false)
        String attrSerializeOnly = "serialize";
        @JsonApiAttribute(serialize = false)
        String attrDeserializeOnly = "deserialize";
    }
}
