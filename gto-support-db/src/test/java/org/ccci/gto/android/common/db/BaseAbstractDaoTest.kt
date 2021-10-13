package org.ccci.gto.android.common.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.ccci.gto.android.common.db.model.Model1
import org.junit.Before
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.mockito.stubbing.Stubber

abstract class BaseAbstractDaoTest {
    protected lateinit var cursor: Cursor
    protected lateinit var db: SQLiteDatabase
    private lateinit var helper: SQLiteOpenHelper
    protected lateinit var dao: TestDao

    @Before
    fun setupMockDb() {
        cursor = mock()

        db = mock {
            on {
                query(any(), any(), any(), anyOrNull(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
            } doReturn cursor
        }

        helper = mock {
            on { readableDatabase } doReturn db
            on { writableDatabase } doReturn db
        }

        dao = spy(TestDao(helper))
    }

    protected fun Stubber.wheneverGetPrimaryKeyWhere(obj: Any) = whenever(dao).getPrimaryKeyWhere(obj)

    protected class TestDao(helper: SQLiteOpenHelper) : AbstractDao(helper) {
        init {
            registerType(Model1::class.java, Model1.TABLE_NAME, arrayOf(Model1.FIELD_NAME), null, null)
        }

        val invalidatedClasses = mutableListOf<Class<*>>()
        override fun onInvalidateClass(clazz: Class<*>) {
            invalidatedClasses += clazz
        }

        fun invalidate(clazz: Class<*>) = invalidateClass(clazz)

        // expose protected methods to mock in tests
        public override fun getPrimaryKeyWhere(obj: Any) = super.getPrimaryKeyWhere(obj)
    }
}
