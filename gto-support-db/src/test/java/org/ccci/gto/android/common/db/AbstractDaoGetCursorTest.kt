package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.db.model.Model1
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class AbstractDaoGetCursorTest : BaseAbstractDaoTest() {
    @Test
    fun verifyGetCursor() {
        Query.select<Model1>().getCursor(dao)

        argumentCaptor<String> {
            val cols = argumentCaptor<Array<String>>()
            verify(db).query(
                any(), capture(), cols.capture(), anyOrNull(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
            )
            assertEquals(Model1.TABLE_NAME, firstValue)
            assertThat(cols.firstValue.toList(), contains(Model1.FIELD_NAME))
        }
    }

    @Test
    fun verifyGetCursorWhere() {
        Query.select<Model1>()
            .where(Expression.raw("a = ?", "arg1"))
            .getCursor(dao)

        argumentCaptor<String> {
            val args = argumentCaptor<Array<String>>()
            verify(db).query(
                any(), any(), any(), capture(), args.capture(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
            )
            assertEquals("a = ?", firstValue)
            assertThat(args.firstValue.toList(), contains("arg1"))
        }
    }

    @Test
    fun verifyGetCursorGroupByHaving() {
        Query.select<Model1>()
            .where(Expression.raw("a = ?", "whereArg"))
            .groupBy(Model1.FIELD).having(Expression.raw("b = ?", "havingArg"))
            .getCursor(dao)

        argumentCaptor<String> {
            val having = argumentCaptor<String>()
            val args = argumentCaptor<Array<String>>()
            verify(db).query(
                any(), any(), any(), any(), args.capture(), capture(), having.capture(), anyOrNull(), anyOrNull()
            )

            assertEquals("${Model1.TABLE_NAME}.${Model1.FIELD_NAME}", firstValue)
            assertEquals("b = ?", having.firstValue)
            assertThat(args.firstValue.toList(), contains("whereArg", "havingArg"))
        }
    }
}
