package org.ccci.gto.android.common.jsonapi

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.ccci.gto.android.common.jsonapi.JsonApiConverter.Options
import org.ccci.gto.android.common.jsonapi.util.Includes

class JsonApiConverterOptionsTest {
    @Test
    fun `include()`() {
        val options = Options.builder()
            .include("a")
            .include(Includes("b"))
            .build()

        assertEquals(setOf("a", "b"), options.mIncludes!!.include)
    }

    @Test
    fun `includeAll()`() {
        val options = Options.builder()
            .include("a")
            .includeAll()
            .include("b")
            .build()

        assertNull(options.mIncludes)
    }
}
