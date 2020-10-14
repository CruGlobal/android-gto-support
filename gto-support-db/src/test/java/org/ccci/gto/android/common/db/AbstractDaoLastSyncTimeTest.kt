package org.ccci.gto.android.common.db

import android.content.ContentValues
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.CommonTables.LastSyncTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AbstractDaoLastSyncTimeTest : BaseAbstractDaoTest() {
    @Test
    fun verifyUpdateLastSyncTime() {
        val start = System.currentTimeMillis()
        dao.updateLastSyncTime("a", 1, true)
        val end = System.currentTimeMillis()

        argumentCaptor<ContentValues> {
            verify(db).replace(eq(LastSyncTable.TABLE_NAME), eq(null), capture())
            assertEquals("a:1:true", firstValue.get(LastSyncTable.COLUMN_KEY))
            assertThat(
                firstValue.getAsLong(LastSyncTable.COLUMN_LAST_SYNCED),
                allOf(greaterThanOrEqualTo(start), lessThanOrEqualTo(end))
            )
        }
    }

    @Test
    fun verifyGetLastSyncTime() {
        cursor.stub {
            on { moveToFirst() } doReturn true
            on { getColumnIndex(LastSyncTable.COLUMN_LAST_SYNCED) } doReturn 0
            on { getString(0) } doReturn "5"
        }

        assertEquals(5, dao.getLastSyncTime("a", 1, true))
        argumentCaptor<Query<LastSyncTable>> {
            verify(dao).getCursor(capture())

            val where = firstValue.buildSqlWhere(dao)!!
            assertThat(where.sql, equalTo(LastSyncTable.SQL_WHERE_PRIMARY_KEY.buildSql(dao).sql))
            assertThat(where.args.toList(), contains("a:1:true"))
        }
    }

    @Test
    fun verifyGetLastSyncTimeNotSet() {
        whenever(cursor.moveToFirst()) doReturn false

        assertEquals(0, dao.getLastSyncTime("a"))
        verify(dao).getCursor(any<Query<*>>())
        verify(cursor, never()).getColumnIndex(any())
        verify(cursor, never()).getString(any())
    }
}
