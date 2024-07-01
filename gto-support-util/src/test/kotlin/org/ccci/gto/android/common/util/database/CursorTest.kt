package org.ccci.gto.android.common.util.database

import android.database.Cursor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertNull

private const val VALID = "valid"
private const val INVALID = "invalid"
private const val VALID_INDEX = 0

class CursorTest {
    private val cursor: Cursor = mockk {
        every { getColumnIndex(VALID) } returns VALID_INDEX
        every { getColumnIndex(INVALID) } returns -1
        every { getString(-1) } throws RuntimeException()
    }

    // region getJSONArrayOrNull()
    @Test
    fun verifyGetJSONArrayOrNull() {
        every { cursor.getString(VALID_INDEX) } returns "[\"a\", 1]"
        assertThat(cursor.getJSONArrayOrNull(VALID), jsonEquals(JSONArray(listOf("a", 1))))
    }

    @Test
    fun testGetJSONArrayWhenNullValue() {
        every { cursor.getString(VALID_INDEX) } returns null
        assertNull(cursor.getJSONArrayOrNull(VALID))
    }

    @Test
    fun verifyGetJSONArrayOrNullWhenInvalidValue() {
        every { cursor.getString(VALID_INDEX) } returns "{\"a\":1"
        assertNull(cursor.getJSONArrayOrNull(VALID))
    }

    @Test
    fun verifyGetJSONArrayOrNullWhenNonExistentField() {
        assertNull(cursor.getJSONArrayOrNull(INVALID))
        verify(exactly = 0) { cursor.getString(any()) }
    }
    // endregion getJSONArrayOrNull()

    // region getJSONObjectOrNull()
    @Test
    fun verifyGetJSONObjectOrNull() {
        every { cursor.getString(VALID_INDEX) } returns "{\"a\":1}"
        assertThat(cursor.getJSONObjectOrNull(VALID), jsonEquals(JSONObject(mapOf("a" to 1))))
    }

    @Test
    fun testGetJSONObjectWhenNullValue() {
        every { cursor.getString(VALID_INDEX) } returns null
        assertNull(cursor.getJSONObjectOrNull(VALID))
    }

    @Test
    fun verifyGetJSONObjectOrNullWhenInvalidValue() {
        every { cursor.getString(VALID_INDEX) } returns "{\"a\":1"
        assertNull(cursor.getJSONObjectOrNull(VALID))
    }

    @Test
    fun verifyGetJSONObjectOrNullWhenNonExistentField() {
        assertNull(cursor.getJSONObjectOrNull(INVALID))
        verify(exactly = 0) { cursor.getString(any()) }
    }
    // endregion getJSONObjectOrNull()

    // region getLong()
    @Test
    fun testGetLong() {
        every { cursor.getString(VALID_INDEX) } returns "1"
        assertEquals(1L, cursor.getLong(VALID))
    }

    @Test
    fun testGetLongDefaultWhenNullValue() {
        every { cursor.getString(VALID_INDEX) } returns null
        assertNull(cursor.getLong(VALID))
        assertEquals(1, cursor.getLong(VALID, 1))
        assertNull(cursor.getLong(VALID, null))
    }

    @Test
    fun testGetLongDefaultWhenInvalidValue() {
        every { cursor.getString(VALID_INDEX) } returns "abcde"
        assertNull(cursor.getLong(VALID))
        assertEquals(1, cursor.getLong(VALID, 1))
        assertNull(cursor.getLong(VALID, null))
    }

    @Test
    fun testGetLongDefaultWhenNonExistentField() {
        assertNull(cursor.getLong(INVALID))
        assertEquals(1, cursor.getLong(INVALID, 1))
        assertNull(cursor.getLong(INVALID, null))
        verify(exactly = 0) { cursor.getString(any()) }
    }
    // endregion getLong()

    // region getString()
    @Test
    fun testGetString() {
        every { cursor.getString(VALID_INDEX) } returns "string"
        assertEquals("string", cursor.getString(VALID))
    }

    @Test
    fun testGetStringDefaultWhenNullValue() {
        every { cursor.getString(VALID_INDEX) } returns null
        assertNull(cursor.getString(VALID))
        assertEquals("default", cursor.getString(VALID, "default"))
        assertNull(cursor.getString(VALID, null))
    }

    @Test
    fun testGetStringDefaultWhenNonExistentField() {
        assertNull(cursor.getString(INVALID))
        assertEquals("default", cursor.getString(INVALID, "default"))
        assertNull(cursor.getString(INVALID, null))
        verify(exactly = 0) { cursor.getString(any()) }
    }
    // endregion getString()
}
