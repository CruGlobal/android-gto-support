package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodePresent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class JsonApiConverterSparseFieldsetTest {
    @Test
    public void verifyToJsonSparseFieldsetSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder()
                .addClasses(ModelParent.class, ModelChild.class)
                .build();

        final ModelParent parent = createObj();
        final String json = converter.toJson(JsonApiObject.single(parent),
                                             Options.builder()
                                                     .fields(ModelParent.TYPE, "name")
                                                     .build());
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThatJson(json).node("data.attributes").matches(jsonEquals("{name:'Padre'}"));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.relationships.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonNodeAbsent("data.relationships.children"));
        assertThat(json, jsonNodeAbsent("included"));
    }

    @Test
    public void verifyToJsonSparseFieldsetRelated() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder()
                .addClasses(ModelParent.class, ModelChild.class)
                .build();

        final ModelParent parent = createObj();
        final String json = converter.toJson(JsonApiObject.single(parent),
                                             Options.builder()
                                                     .fields(ModelChild.TYPE, "name")
                                                     .build());
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThatJson(json).node("included").isArray().ofLength(2);
        assertThatJson(json).node("included")
                .matches(hasItem(jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}")));
        assertThatJson(json).node("included")
                .matches(hasItem(jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}")));

    }

    @Test
    public void verifyToJsonSparseFieldsetRelations() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder()
                .addClasses(ModelParent.class, ModelChild.class)
                .build();

        final ModelParent parent = createObj();
        final String json = converter.toJson(JsonApiObject.single(parent),
                                             Options.builder()
                                                     .fields(ModelParent.TYPE, "name", "favorite")
                                                     .fields(ModelChild.TYPE, "name")
                                                     .build());
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThatJson(json).node("data.attributes").matches(jsonEquals("{name:'Padre'}"));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodePresent("data.relationships.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonNodeAbsent("data.relationships.children"));
        assertThatJson(json).node("included").isArray().ofLength(1);
        assertThatJson(json).node("included")
                .matches(hasItem(jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}")));
        assertThatJson(json).node("included")
                .matches(not(hasItem(jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}"))));
    }

    private ModelParent createObj() {
        final ModelParent parent = new ModelParent("Padre", 65);
        parent.favorite = new ModelChild("Daniel", 34);
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You", 15);
        child2.id = 20;
        parent.children.add(child2);
        return parent;
    }

    @JsonApiType(ModelParent.TYPE)
    public static final class ModelParent extends ModelBase {
        static final String TYPE = "parent";

        String name;
        long age;

        public ModelParent() {}

        public ModelParent(final String name, final long age) {
            this.name = name;
            this.age = age;
        }

        List<ModelChild> children = new ArrayList<>();

        // everyone has a favorite child
        ModelChild favorite;
    }

    @JsonApiType(ModelChild.TYPE)
    public static final class ModelChild extends ModelBase {
        static final String TYPE = "child";

        String name;
        long age;

        public ModelChild() {}

        public ModelChild(final String name, final long age) {
            this.name = name;
            this.age = age;
        }
    }
}
