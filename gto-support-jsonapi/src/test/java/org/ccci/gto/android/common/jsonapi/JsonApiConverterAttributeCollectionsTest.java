package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import androidx.annotation.Nullable;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class JsonApiConverterAttributeCollectionsTest {
    private final JsonApiConverter mConverter =
            new JsonApiConverter.Builder().addClasses(ModelCollectionAttribute.class).build();

    @Test
    public void verifyToJsonNull() throws Exception {
        final ModelCollectionAttribute obj = new ModelCollectionAttribute(99);
        final String json = mConverter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelCollectionAttribute.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThat(json, jsonNodeAbsent("data.attributes.stringSet"));
        assertThat(json, jsonNodeAbsent("data.attributes.integerList"));
    }

    @Test
    public void verifyToJsonEmpty() throws Exception {
        final ModelCollectionAttribute obj = new ModelCollectionAttribute(99);
        obj.stringSet = new HashSet<>();
        obj.integerList = new Vector<>();
        final String json = mConverter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelCollectionAttribute.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThatJson(json).node("data.attributes.stringSet").isArray().ofLength(0);
        assertThatJson(json).node("data.attributes.integerList").isArray().ofLength(0);
    }

    @Test
    public void verifyToJson() throws Exception {
        final ModelCollectionAttribute obj = new ModelCollectionAttribute(99);
        obj.integerList = Arrays.asList(1, 2, 3);
        obj.stringSet = new HashSet<>(Arrays.asList("a", "b", "c", "a"));
        final String json = mConverter.toJson(JsonApiObject.single(obj));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelCollectionAttribute.TYPE));
        assertThat(json, jsonPartEquals("data.id", obj.id));
        assertThatJson(json).node("data.attributes.integerList").isArray().ofLength(3)
                .thatContains(1)
                .thatContains(2)
                .thatContains(3);
        assertThatJson(json).node("data.attributes.stringSet").isArray().ofLength(3)
                .thatContains("a")
                .thatContains("b")
                .thatContains("c");
    }

    @Test
    public void verifyFromJsonNull() throws Exception {
        final ModelCollectionAttribute in = new ModelCollectionAttribute(99);
        final String json = mConverter.toJson(JsonApiObject.single(in));
        final ModelCollectionAttribute out = mConverter.fromJson(json, ModelCollectionAttribute.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.integerList, nullValue());
        assertThat(out.stringSet, nullValue());
    }

    @Test
    public void verifyFromJsonEmpty() throws Exception {
        final ModelCollectionAttribute in = new ModelCollectionAttribute(99);
        in.stringSet = new HashSet<>();
        in.integerList = new Vector<>();
        final String json = mConverter.toJson(JsonApiObject.single(in));
        final ModelCollectionAttribute out = mConverter.fromJson(json, ModelCollectionAttribute.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.stringSet, not(nullValue()));
        assertThat(out.stringSet.size(), is(0));
        assertThat(out.integerList, not(nullValue()));
        assertThat(out.integerList.size(), is(0));
    }

    @Test
    public void verifyFromJson() throws Exception {

        final ModelCollectionAttribute in = new ModelCollectionAttribute(99);
        in.integerList = Arrays.asList(1, 2, 3);
        in.stringSet = new HashSet<>(Arrays.asList("a", "b", "c", "a"));
        final String json = mConverter.toJson(JsonApiObject.single(in));
        final ModelCollectionAttribute out = mConverter.fromJson(json, ModelCollectionAttribute.class).getDataSingle();
        assertThat(out, not(nullValue()));
        assertThat(out.id, is(in.id));
        assertThat(out.stringSet, not(nullValue()));
        assertThat(out.stringSet.size(), is(3));
        assertThat(out.stringSet, containsInAnyOrder("c", "b", "a"));
        assertThat(out.integerList, not(nullValue()));
        assertThat(out.integerList.size(), is(3));
        assertThat(out.integerList, contains(1, 2, 3));
    }

    @JsonApiType(ModelCollectionAttribute.TYPE)
    public static class ModelCollectionAttribute extends ModelBase {
        public static final String TYPE = "collection_attribute";

        public ModelCollectionAttribute() {}

        public ModelCollectionAttribute(final int id) {
            super(id);
        }

        @Nullable
        Set<String> stringSet;
        @Nullable
        List<Integer> integerList;
    }
}
