package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.ccci.gto.android.common.db.model.Model1
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class AbstractDaoTest : BaseAbstractDaoTest() {
    @Test
    fun verifyTableName() {
        assertEquals(Model1.TABLE_NAME, dao.tableName(Model1::class.java))
    }

    @Test
    fun verifyRefresh() {
        val model = Model1()
        doReturn(Expression.NULL).wheneverGetPrimaryKeyWhere(eq(model))

        dao.refresh(model)
        argumentCaptor<Query<Model1>> {
            verify(dao).getPrimaryKeyWhere(model)
            verify(dao).getCursor(capture())

            assertEquals(Expression.NULL, firstValue.where)
        }
    }
}
