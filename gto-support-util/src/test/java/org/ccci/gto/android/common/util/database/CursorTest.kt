package org.ccci.gto.android.common.util.database

import android.database.Cursor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt

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

    // region getJSONObject()
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
        verify(cursor, never()).getString(anyInt())
    }
    // endregion getJSONObject()

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
        verify(cursor, never()).getString(anyInt())
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
        verify(cursor, never()).getString(anyInt())
    }
    // endregion getString()

    private fun wheneverGetValid() = whenever(cursor.getString(0))
}
