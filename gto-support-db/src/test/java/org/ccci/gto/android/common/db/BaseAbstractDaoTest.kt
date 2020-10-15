package org.ccci.gto.android.common.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.model.Model1
import org.junit.Before
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
