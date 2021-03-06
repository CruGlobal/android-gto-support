package org.ccci.gto.android.common.jsonapi.retrofit2;

import org.junit.Test;

import static org.ccci.gto.android.common.jsonapi.retrofit2.BaseJsonApiParamsKt.PARAM_INCLUDE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class JsonApiParamsTest {
    @Test
    public void verifyInclude() throws Exception {
        final JsonApiParams params = new JsonApiParams();
        params.put("param1", "value");
        params.include("a", "a.b", "c");

        assertThat(params.size(), is(2));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(PARAM_INCLUDE), is("a,a.b,c"));
        assertThat(params.get("param2"), is(nullValue()));

        // make sure other params don't interfere with the include param
        params.put("param2", "value");
        assertThat(params.size(), is(3));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(PARAM_INCLUDE), is("a,a.b,c"));
        assertThat(params.get("param2"), is("value"));

        // add more includes
        params.include(null, "d");
        assertThat(params.size(), is(3));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(PARAM_INCLUDE), is("a,a.b,c,d"));
        assertThat(params.get("param2"), is("value"));
    }

    @Test
    public void verifyClearIncludes() throws Exception {
        final JsonApiParams params = new JsonApiParams();
        params.include("a");
        assertThat(params.size(), is(1));
        assertThat(params.get(PARAM_INCLUDE), is("a"));

        params.clearIncludes();
        assertThat(params.size(), is(0));
        assertThat(params.get(PARAM_INCLUDE), is(nullValue()));
    }

    @Test
    public void verifyFields() throws Exception {
        final JsonApiParams params = new JsonApiParams();
        params.fields("a", "a1", "a2");
        params.fields("b", "b1", "b2");
        params.fields("a", "c1", "c2");
        params.put("param1", "value");

        assertThat(params.size(), is(3));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get("fields[a]"), is("a1,a2,c1,c2"));
        assertThat(params.get("fields[b]"), is("b1,b2"));
        assertThat(params.get("param2"), is(nullValue()));
    }
}
