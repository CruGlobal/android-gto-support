package org.ccci.gto.android.common.jsonapi;

import android.support.test.runner.AndroidJUnit4;

import org.ccci.gto.android.common.jsonapi.model.JsonApiError;
import org.ccci.gto.android.common.jsonapi.model.JsonApiObject;
import org.ccci.gto.android.common.jsonapi.model.ModelSimple;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class JsonApiConverterErrorsIT {
    @Test
    public void verifyToJsonSingleSimpleError() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        final JsonApiError error = new JsonApiError();
        error.setDetail("Detail human readable message.");
        error.setStatus(200);
        final String json = converter.toJson(JsonApiObject.error(error));
        assertThatJson(json).node("data").isAbsent();
        assertThatJson(json).node("errors").isPresent();
        assertThatJson(json).node("errors").isArray().ofLength(1);
        assertThat(json, jsonPartEquals("errors[0].detail", error.getDetail()));
        assertThat(json, jsonPartEquals("errors[0].status", "\"" + error.getStatus() + "\""));
    }

    @Test
    public void verifyFromJsonSingleSimpleError() throws Exception {
        final JsonApiConverter converter = new JsonApiConverter.Builder().addClasses(ModelSimple.class).build();

        final JsonApiError error = new JsonApiError();
        error.setDetail("Detail human readable message.");
        error.setStatus(200);
        final String json = converter.toJson(JsonApiObject.error(error));

        final JsonApiObject<ModelSimple> obj = converter.fromJson(json, ModelSimple.class);
        assertThat(obj.hasErrors(), is(true));
        assertThat(obj.getErrors(), hasSize(1));
        final JsonApiError error1 = obj.getErrors().get(0);
        assertThat(error1.getDetail(), is(error.getDetail()));
        assertThat(error1.getStatus(), is(error.getStatus()));
    }
}
