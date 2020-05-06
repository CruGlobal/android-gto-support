package org.ccci.gto.android.common.util.database

import android.database.Cursor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt

private const val VALID = "valid"
private const val INVALID = "invalid"

class CursorUtilsTest {
    private lateinit var cursor: Cursor

    @Before
    fun setup() {
        cursor = mock()
        whenever(cursor.getColumnIndex(VALID)).thenReturn(0)
        whenever(cursor.getColumnIndex(INVALID)).thenReturn(-1)

        whenever(cursor.getString(-1)).thenThrow(RuntimeException::class.java)
    }

    // region getString()
    @Test
    fun testGetString() {
        wheneverGetStringValid().thenReturn("string")
        assertEquals("string", cursor.getString(VALID))
    }

    @Test
    fun testGetStringDefaultWhenNullValue() {
        wheneverGetStringValid().thenReturn(null)
        assertNull(cursor.getString(VALID))
        assertEquals("default", cursor.getString(VALID, "default"))
        assertNull(cursor.getString(VALID, null))
    }

    @Test
    fun testGetStringDefaultWhenNonExistentField() {
        wheneverGetStringValid().thenReturn("")
        assertNull(cursor.getString(INVALID))
        assertEquals("default", cursor.getString(INVALID, "default"))
        assertNull(cursor.getString(INVALID, null))
        verify(cursor, never()).getString(anyInt())
    }
    // endregion getString()

    private fun wheneverGetStringValid() = whenever(cursor.getString(0))
}
