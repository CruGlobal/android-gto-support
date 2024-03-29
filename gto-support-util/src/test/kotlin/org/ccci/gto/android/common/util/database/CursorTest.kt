package org.ccci.gto.android.common.util.database

import android.database.Cursor
import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private const val VALID = "valid"
private const val INVALID = "invalid"

class CursorTest {
    private lateinit var cursor: Cursor

    @Before
    fun setup() {
        cursor = mock()
        whenever(cursor.getColumnIndex(VALID)).thenReturn(0)
        whenever(cursor.getColumnIndex(INVALID)).thenReturn(-1)

        whenever(cursor.getString(-1)).thenThrow(RuntimeException::class.java)
    }

    // region getJSONArrayOrNull()
    @Test
    fun verifyGetJSONArrayOrNull() {
        wheneverGetValid().thenReturn("[\"a\", 1]")
        assertThat(cursor.getJSONArrayOrNull(VALID), jsonEquals(JSONArray(listOf("a", 1))))
    }

    @Test
    fun testGetJSONArrayWhenNullValue() {
        wheneverGetValid().thenReturn(null)
        assertNull(cursor.getJSONArrayOrNull(VALID))
    }

    @Test
    fun verifyGetJSONArrayOrNullWhenInvalidValue() {
        wheneverGetValid().thenReturn("{\"a\":1")
        assertNull(cursor.getJSONArrayOrNull(VALID))
    }

    @Test
    fun verifyGetJSONArrayOrNullWhenNonExistentField() {
        assertNull(cursor.getJSONArrayOrNull(INVALID))
        verify(cursor, never()).getString(any())
    }
    // endregion getJSONArrayOrNull()

    // region getJSONObjectOrNull()
    @Test
    fun verifyGetJSONObjectOrNull() {
        wheneverGetValid().thenReturn("{\"a\":1}")
        assertThat(cursor.getJSONObjectOrNull(VALID), jsonEquals(JSONObject(mapOf("a" to 1))))
    }

    @Test
    fun testGetJSONObjectWhenNullValue() {
        wheneverGetValid().thenReturn(null)
        assertNull(cursor.getJSONObjectOrNull(VALID))
    }

    @Test
    fun verifyGetJSONObjectOrNullWhenInvalidValue() {
        wheneverGetValid().thenReturn("{\"a\":1")
        assertNull(cursor.getJSONObjectOrNull(VALID))
    }

    @Test
    fun verifyGetJSONObjectOrNullWhenNonExistentField() {
        assertNull(cursor.getJSONObjectOrNull(INVALID))
        verify(cursor, never()).getString(any())
    }
    // endregion getJSONObjectOrNull()

    // region getLong()
    @Test
    fun testGetLong() {
        wheneverGetValid().thenReturn("1")
        assertEquals(1L, cursor.getLong(VALID))
    }

    @Test
    fun testGetLongDefaultWhenNullValue() {
        wheneverGetValid().thenReturn(null)
        assertNull(cursor.getLong(VALID))
        assertEquals(1, cursor.getLong(VALID, 1))
        assertNull(cursor.getLong(VALID, null))
    }

    @Test
    fun testGetLongDefaultWhenInvalidValue() {
        wheneverGetValid().thenReturn("abcde")
        assertNull(cursor.getLong(VALID))
        assertEquals(1, cursor.getLong(VALID, 1))
        assertNull(cursor.getLong(VALID, null))
    }

    @Test
    fun testGetLongDefaultWhenNonExistentField() {
        assertNull(cursor.getLong(INVALID))
        assertEquals(1, cursor.getLong(INVALID, 1))
        assertNull(cursor.getLong(INVALID, null))
        verify(cursor, never()).getString(any())
    }
    // endregion getLong()

    // region getString()
    @Test
    fun testGetString() {
        wheneverGetValid().thenReturn("string")
        assertEquals("string", cursor.getString(VALID))
    }

    @Test
    fun testGetStringDefaultWhenNullValue() {
        wheneverGetValid().thenReturn(null)
        assertNull(cursor.getString(VALID))
        assertEquals("default", cursor.getString(VALID, "default"))
        assertNull(cursor.getString(VALID, null))
    }

    @Test
    fun testGetStringDefaultWhenNonExistentField() {
        assertNull(cursor.getString(INVALID))
        assertEquals("default", cursor.getString(INVALID, "default"))
        assertNull(cursor.getString(INVALID, null))
        verify(cursor, never()).getString(any())
    }
    // endregion getString()

    private fun wheneverGetValid() = whenever(cursor.getString(0))
}
