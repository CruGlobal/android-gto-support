package org.ccci.gto.android.common.jsonapi.retrofit2;

import android.text.TextUtils;

import org.ccci.gto.android.common.testing.CommonMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class JsonApiParamsTest {
    @Before
    public void setup() throws Exception {
        CommonMocks.mockTextUtils();
    }

    @Test
    public void verifyInclude() throws Exception {
        final JsonApiParams params = new JsonApiParams();
        params.put("param1", "value");
        params.include("a", "a.b", "c");

        assertThat(params.size(), is(2));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(JsonApiParams.PARAM_INCLUDE), is("a,a.b,c"));
        assertThat(params.get("param2"), is(nullValue()));

        // make sure other params don't interfere with the include param
        params.put("param2", "value");
        assertThat(params.size(), is(3));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(JsonApiParams.PARAM_INCLUDE), is("a,a.b,c"));
        assertThat(params.get("param2"), is("value"));

        // add more includes
        params.include(null, "d");
        assertThat(params.size(), is(3));
        assertThat(params.get("param1"), is("value"));
        assertThat(params.get(JsonApiParams.PARAM_INCLUDE), is("a,a.b,c,d"));
        assertThat(params.get("param2"), is("value"));
    }

    @Test
    public void verifyClearIncludes() throws Exception {
        final JsonApiParams params = new JsonApiParams();
        params.include("a");
        assertThat(params.size(), is(1));
        assertThat(params.get(JsonApiParams.PARAM_INCLUDE), is("a"));

        params.clearIncludes();
        assertThat(params.size(), is(0));
        assertThat(params.get(JsonApiParams.PARAM_INCLUDE), is(nullValue()));
    }
}
