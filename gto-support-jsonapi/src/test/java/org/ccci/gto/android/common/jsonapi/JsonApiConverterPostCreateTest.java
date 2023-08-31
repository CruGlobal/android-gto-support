package org.ccci.gto.android.common.jsonapi;

import org.ccci.gto.android.common.jsonapi.annotation.JsonApiIgnore;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiPostCreate;
import org.ccci.gto.android.common.jsonapi.annotation.JsonApiType;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelBase;
import org.ccci.gto.android.common.jsonapi.model.ModelSimple;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JsonApiConverterPostCreateTest {
    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterPostCreateParams() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateParameters.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterPostCreateCheckedException() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateCheckedException.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterPostCreateNotFinalMethod() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateNotFinalClass.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyConverterPostCreateStaticMethod() throws Exception {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateStaticMethod.class).build();
    }

    @Test
    public void verifyConverterPostCreateFinalClass() {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateFinalClass.class).build();
    }

    @Test
    public void verifyConverterPostCreateFinalMethod() {
        new JsonApiConverter.Builder().addClasses(ModelPostCreateFinalClass.class).build();
    }

    @Test
    public void verifyConverterPostCreatePrivateMethod() {
        new JsonApiConverter.Builder().addClasses(ModelPostCreatePrivateMethod.class).build();
    }

    @Test
    public void verifyConverterPostCreateSimple() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        final ModelSimple source = new ModelSimple(99);
        final JsonApiObject<ModelSimple> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(source)), ModelSimple.class);
        assertThat(output.isSingle(), is(true));
        assertThat(output.getDataSingle(), is(not(nullValue())));
        assertThat(output.getDataSingle().id, is(99));
        assertTrue(output.getDataSingle().postCreateCalled);
    }

    @Test
    public void verifyConverterPostCreateMultiple() throws Exception {
        final JsonApiConverter converter =
                new JsonApiConverter.Builder().addClasses(ModelDuplicatePostCreate.class).build();

        final ModelDuplicatePostCreate source = new ModelDuplicatePostCreate();
        final JsonApiObject<ModelDuplicatePostCreate> output =
                converter.fromJson(converter.toJson(JsonApiObject.single(source)), ModelDuplicatePostCreate.class);
        assertThat(output.isSingle(), is(true));
        assertThat(output.getDataSingle(), is(not(nullValue())));
        assertTrue(output.getDataSingle().postCreateCalled);
        assertTrue(output.getDataSingle().mPrivatePostCreateCalled);
    }

    @JsonApiType("duplicate")
    public static class ModelDuplicatePostCreate extends ModelBase {
        @JsonApiIgnore
        public boolean mPrivatePostCreateCalled = false;

        @JsonApiPostCreate
        private void privatePostCreate() {
            if (mPrivatePostCreateCalled) {
                throw new IllegalStateException("privatePostCreate() should only be called once");
            }
            mPrivatePostCreateCalled = true;
        }
    }

    @JsonApiType("params")
    private static class ModelPostCreateParameters {
        @JsonApiPostCreate
        private void postCreate(final String param1) {}
    }

    @JsonApiType("exception")
    private static class ModelPostCreateCheckedException {
        @JsonApiPostCreate
        private void postCreate() throws Exception {}
    }

    @JsonApiType("staticmethod")
    private static final class ModelPostCreateStaticMethod {
        @JsonApiPostCreate
        public static void postCreate() {}
    }

    @JsonApiType("notfinalclass")
    private static class ModelPostCreateNotFinalClass {
        @JsonApiPostCreate
        public void postCreate() {}
    }

    @JsonApiType("finalclass")
    private static final class ModelPostCreateFinalClass {
        @JsonApiPostCreate
        private void postCreate() {}
    }

    @JsonApiType("finalmethod")
    private static class ModelPostCreateFinalMethod {
        @JsonApiPostCreate
        public final void postCreate() {}
    }

    @JsonApiType("finalmethod")
    private static class ModelPostCreatePrivateMethod {
        @JsonApiPostCreate
        private void postCreate() {}
    }
}
