package org.ccci.gto.android.common.jsonapi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldsTest {
    @Test
    fun verifyFieldsAll() {
        val fields = JsonApiConverter.Fields(null)
        assertTrue(fields.include("asdfjk"))
        assertTrue(fields.include("wije"))
    }

    @Test
    fun verifyFields() {
        val fields = fields("a", "b")
        assertTrue(fields.include("a"))
        assertTrue(fields.include("b"))
        assertFalse(fields.include("c"))
    }

    private fun fields(vararg fields: String) = JsonApiConverter.Fields(fields.toList())
}
