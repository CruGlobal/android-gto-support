package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPlaceholder;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonNodeAbsent;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_EXTRA_FIELDS;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options.include;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;

public class JsonApiConverterRelatedTest {
    @Test
    public void verifyToJsonRelationships() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You");
        child2.id = 20;
        parent.children.add(child2);
        final ModelChild child3 = new ModelChild("Child with no name");
        parent.children.add(child3);
        parent.orphans = new ModelChild[] {parent.favorite, child2, child3};

        final String json = converter.toJson(JsonApiObject.single(parent));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.type", ModelChild.TYPE));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.id", parent.favorite.id));
        assertThat(json, jsonNodeAbsent("data.relationships.favorite.data.attributes"));
        assertThatJson(json).node("data.relationships.children.data").isArray().ofLength(2);
        assertThatJson(json).node("data.relationships.orphans.data").isArray().ofLength(2);
        assertThatJson(json).node("included").isArray().ofLength(2);
        assertThatJson(json).node("included").matches(
                hasItem(jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}").when(IGNORING_EXTRA_FIELDS)));
        assertThatJson(json).node("included").matches(
                hasItem(jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}").when(IGNORING_EXTRA_FIELDS)));
    }

    @Test
    public void verifyFromJsonRelationships() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Kid");
        child2.id = 20;
        parent.children.add(child2);
        parent.orphans = new ModelChild[] {parent.favorite, child2};

        final JsonApiObject<ModelParent> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(parent)), ModelParent.class);
        assertThat(output.isSingle(), is(true));
        final ModelParent target = output.getDataSingle();
        assertThat(target, is(not(nullValue())));
        assertThat(target.id, is(parent.id));
        assertThat(target.favorite, is(not(nullValue())));
        assertThat(target.favorite.id, is(parent.favorite.id));
        assertThat(target.favorite.name, is(parent.favorite.name));
        assertThat(target.children.size(), is(2));
        assertThat(target.children.get(0), is(sameInstance(target.favorite)));
        assertThat(target.children, hasItems(parent.children.toArray(new ModelChild[0])));
        assertArrayEquals(parent.orphans, target.orphans);
    }

    @Test
    public void verifyFromJsonRelationshipsMissingJson() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final String raw = "{\"data\":{\"type\":\"parent\",\"id\":1}}";
        final JsonApiObject<ModelParent> output = converter.fromJson(raw, ModelParent.class);
        assertThat(output.isSingle(), is(true));
        final ModelParent target = output.getDataSingle();
        assertThat(target.id, is(1));
        assertThat(target.favorite, is(nullValue()));
    }

    @Test
    public void verifyToJsonIncludeNothing() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You");
        child2.id = 20;
        parent.children.add(child2);

        final String json = converter.toJson(JsonApiObject.single(parent), include());
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.type", ModelChild.TYPE));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.id", parent.favorite.id));
        assertThat(json, jsonNodeAbsent("data.relationships.favorite.data.attributes"));
        assertThatJson(json).node("data.relationships.children.data").isArray().ofLength(2);
        assertThat(json, jsonNodeAbsent("included"));
    }

    @Test
    public void verifyToJsonIncludePartial() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You");
        child2.id = 20;
        parent.children.add(child2);

        final String json = converter.toJson(JsonApiObject.single(parent), include("favorite"));
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.type", ModelChild.TYPE));
        assertThat(json, jsonPartEquals("data.relationships.favorite.data.id", parent.favorite.id));
        assertThat(json, jsonNodeAbsent("data.relationships.favorite.data.attributes"));
        assertThatJson(json).node("data.relationships.children.data").isArray().ofLength(2);
        assertThatJson(json).node("included").isArray().ofLength(1);
        assertThatJson(json).node("included").matches(
                hasItem(jsonEquals("{type:'child',id:11,attributes:{name:'Daniel'}}").when(IGNORING_EXTRA_FIELDS)));
        assertThatJson(json).node("included").matches(not(hasItem(
                jsonEquals("{type:'child',id:20,attributes:{name:'Hey You'}}").when(IGNORING_EXTRA_FIELDS))));
    }

    @Test
    public void verifyFromJsonIncludePartial() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.favorite.id = 11;
        parent.children.add(parent.favorite);
        final ModelChild child2 = new ModelChild("Hey You");
        child2.id = 20;
        parent.children.add(child2);

        final JsonApiObject<ModelParent> output = converter
                .fromJson(converter.toJson(JsonApiObject.single(parent), include("favorite")), ModelParent.class);
        assertThat(output.isSingle(), is(true));
        final ModelParent target = output.getDataSingle();
        assertThat(target, is(not(nullValue())));
        assertThat(target.id, is(parent.id));
        assertThat(target.placeholder, is(false));
        assertThat(target.favorite, is(not(nullValue())));
        assertThat(target.favorite.placeholder, is(false));
        assertThat(target.favorite.id, is(parent.favorite.id));
        assertThat(target.favorite.name, is(parent.favorite.name));
        assertThat(target.children.size(), is(2));
        assertThat(target.children.get(0).placeholder, is(false));
        assertThat(target.children.get(0).postCreateCalled, is(true));
        assertThat(target.children.get(0), is(sameInstance(target.favorite)));
        assertThat(target.children.get(1).placeholder, is(true));
        assertThat(target.children.get(1).postCreateCalled, is(false));
    }

    @Test
    public void verifyToJsonIncludeNoId() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelParent.class, ModelChild.class).build();

        final ModelParent parent = new ModelParent();
        parent.id = 1;
        parent.favorite = new ModelChild("Daniel");
        parent.children.add(parent.favorite);

        final String json = converter.toJson(JsonApiObject.single(parent),
                                             Options.builder().include("favorite").includeObjectsWithNoId(true)
                                                     .build());
        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThat(json, jsonNodeAbsent("data.attributes.favorite"));
        assertThat(json, jsonNodeAbsent("data.attributes.children"));
        assertThat(json, either(jsonNodeAbsent("data.relationships.favorite"))
                .or(jsonPartEquals("data.relationships.favorite.data", "null")));
        assertThatJson(json).node("data.relationships.children.data").isArray().ofLength(0);
        assertThatJson(json).node("included").isArray().ofLength(1);
        assertThatJson(json).node("included").matches(hasItem(jsonEquals("{type:'child',attributes:{name:'Daniel'}}")));
    }

    @JsonApiType(ModelParent.TYPE)
    public static final class ModelParent extends ModelBase {
        static final String TYPE = "parent";

        @JsonApiPlaceholder
        boolean placeholder = false;

        List<ModelChild> children = new ArrayList<>();

        // everyone has a favorite child
        ModelChild favorite;

        ModelChild[] orphans;
    }

    @JsonApiType(ModelChild.TYPE)
    public static final class ModelChild extends ModelBase {
        static final String TYPE = "child";

        @JsonApiPlaceholder
        boolean placeholder = false;

        String name;

        public ModelChild() {}

        public ModelChild(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            ModelChild that = (ModelChild) o;

            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
