package org.ccci.gto.android.common.jsonapi.retrofit2

import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.android.common.jsonapi.retrofit2.JsonApiParams.Companion.PARAM_INCLUDE
import org.ccci.gto.android.common.jsonapi.util.Includes
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.hasEntry

class JsonApiParamsTest {
    @Test
    fun verifyInclude() {
        val params = JsonApiParams()
        params["param1"] = "value"
        params.include("a", "a.b", "c")
        assertThat(
            params,
            allOf(
                aMapWithSize(2),
                hasEntry("param1", "value"),
                hasEntry(PARAM_INCLUDE, "a,a.b,c"),
            ),
        )

        // make sure other params don't interfere with the include param
        params["param2"] = "value"
        assertThat(
            params,
            allOf(
                aMapWithSize(3),
                hasEntry("param1", "value"),
                hasEntry(PARAM_INCLUDE, "a,a.b,c"),
                hasEntry("param2", "value")
            ),
        )

        // add more includes
        params.include("d")
        assertThat(
            params,
            allOf(
                aMapWithSize(3),
                hasEntry("param1", "value"),
                hasEntry(PARAM_INCLUDE, "a,a.b,c,d"),
                hasEntry("param2", "value")
            ),
        )
    }

    @Test
    fun verifyClearIncludes() {
        val params = JsonApiParams()
        params.include("a")
        assertThat(
            params,
            allOf(
                aMapWithSize(1),
                hasEntry(PARAM_INCLUDE, "a"),
            ),
        )
        params.clearIncludes()
        assertThat(params, aMapWithSize(0))
    }

    @Test
    fun verifyIncludes() {
        val values = setOf("a", "a.b", "c", "d")

        val params = JsonApiParams().includes(Includes(values))
        assertEquals(values, params[PARAM_INCLUDE]!!.split(",").toSet())
    }

    @Test
    fun verifyFields() {
        val params = JsonApiParams()
        params.fields("a", "a1", "a2")
        params.fields("b", "b1", "b2")
        params.fields("a", "c1", "c2")
        params["param1"] = "value"
        assertThat(
            params,
            allOf(
                aMapWithSize(3),
                hasEntry("param1", "value"),
                hasEntry("fields[a]", "a1,a2,c1,c2"),
                hasEntry("fields[b]", "b1,b2")
            ),
        )
    }
}
