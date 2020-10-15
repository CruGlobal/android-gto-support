package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import org.ccci.gto.android.common.db.model.Model1
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

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
