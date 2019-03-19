package org.ccci.gto.android.common.jsonapi;

import net.javacrumbs.jsonunit.core.Option;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class JsonApiConverterNullAttributesIT {
    private final JsonApiConverter mConverter = new JsonApiConverter.Builder()
            .addClasses(ModelParent.class, ModelChild.class)
            .build();

    @Test
    public void verifyNullAttributeSerialization() throws Exception {
        final ModelParent parent = createObj();
        final String json = mConverter.toJson(JsonApiObject.single(parent),
                                              JsonApiConverter.Options.builder()
                                                      .serializeNulls(ModelChild.TYPE)
                                                      .build());

        assertThatJson(json).node("data").isObject();
        assertThat(json, jsonPartEquals("data.type", ModelParent.TYPE));
        assertThatJson(json).node("data.attributes").isAbsent();
        assertThatJson(json).node("included").isArray().ofLength(2);
        assertThatJson(json).node("included").when(Option.IGNORING_EXTRA_ARRAY_ITEMS, Option.IGNORING_ARRAY_ORDER)
                .isEqualTo("[{type:'child',id:11,attributes:{name:'Daniel'}}]");
        assertThatJson(json).node("included").when(Option.IGNORING_EXTRA_ARRAY_ITEMS, Option.IGNORING_ARRAY_ORDER)
                .isEqualTo("[{type:'child',id:20,attributes:{name:null}}]");
    }

    private ModelParent createObj() {
        final ModelParent parent = new ModelParent(null);
        parent.children.add(new ModelChild(11, "Daniel"));
        parent.children.add(new ModelChild(20, null));
        return parent;
    }

    @JsonApiType(ModelParent.TYPE)
    public static final class ModelParent extends ModelBase {
        static final String TYPE = "parent";

        String name;

        public ModelParent() {}

        public ModelParent(final String name) {
            this.name = name;
        }

        List<ModelChild> children = new ArrayList<>();
    }

    @JsonApiType(ModelChild.TYPE)
    public static final class ModelChild extends ModelBase {
        static final String TYPE = "child";

        String name;

        public ModelChild() {}

        public ModelChild(final int id, final String name) {
            mId = id;
            this.name = name;
        }
    }
}
