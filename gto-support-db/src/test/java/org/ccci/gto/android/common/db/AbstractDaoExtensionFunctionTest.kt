package org.ccci.gto.android.common.db

import org.junit.Assert.assertEquals
import org.junit.Test

private const val PREFIX = "table."
private const val FIELD1 = "f1"
private const val FIELD2 = "f1"

class AbstractDaoExtensionFunctionTest {
    @Test
    fun testPrefixOrderBySingleField() {
        assertEquals("$PREFIX$FIELD1", FIELD1.prefixOrderByFieldsWith(PREFIX))
    }

    @Test
    fun testPrefixOrderByMultipleFields() {
        assertEquals("$PREFIX$FIELD1,$PREFIX$FIELD2", "$FIELD1,$FIELD2".prefixOrderByFieldsWith(PREFIX))
    }

    @Test
    fun testPrefixOrderByMultipleFieldsSomePrefixed() {
        assertEquals("a.$FIELD1,$PREFIX$FIELD2", "a.$FIELD1,$FIELD2".prefixOrderByFieldsWith(PREFIX))
    }

    @Test
    fun testPrefixOrderByMultipleFieldsAllPrefixed() {
        val raw = "a.$FIELD1,b.$FIELD2"
        assertEquals(raw, raw.prefixOrderByFieldsWith(PREFIX))
    }
}
