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
    fun verifyGetCursorWhere() {
        dao.getCursor(Query.select(Model1::class.java).where(Expression.raw("a = ?", "arg1")))
        argumentCaptor<String> {
            val args = argumentCaptor<Array<String>>()
            verify(db).query(
                any(), any(), any(), capture(), args.capture(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
            )
            assertEquals("a = ?", firstValue)
            assertThat(args.firstValue.toList(), contains("arg1"))
        }
    }
}
