package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteTransactionListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.model.Compound
import org.ccci.gto.android.common.db.model.Root
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Stack

class AbstractDaoClassInvalidationTest {
    private lateinit var db: SQLiteDatabase
    private lateinit var helper: SQLiteOpenHelper
    private lateinit var dao: TestDao

    @Before
    fun setupDao() {
        db = mock()
        helper = mock()
        dao = TestDao(helper)

        whenever(helper.writableDatabase) doReturn db
        db.mockTransactions()
    }

    @Test
    fun verifyInvalidateClassWithoutTransaction() {
        dao.invalidate(Root::class.java)
        assertThat(dao.invalidatedClasses, contains<Class<*>>(Root::class.java))
    }

    @Test
    fun verifyInvalidateClassWithSingleTransaction() {
        dao.transaction {
            dao.invalidate(Root::class.java)
            assertThat(dao.invalidatedClasses, empty())
        }
        assertThat(dao.invalidatedClasses, contains<Class<*>>(Root::class.java))
    }

    @Test
    fun verifyUnsuccessfulInvalidateClassWithSingleTransaction() {
        try {
            dao.transaction {
                dao.invalidate(Root::class.java)
                assertThat(dao.invalidatedClasses, empty())
                throw RuntimeException("expected")
            }
        } catch (e: RuntimeException) {
            assertEquals("expected", e.message)
        }

        assertThat(dao.invalidatedClasses, empty())
    }

    @Test
    fun verifyUnsuccessfulInvalidateClassWithNestedTransaction() {
        dao.transaction {
            dao.invalidate(Root::class.java)
            assertThat(dao.invalidatedClasses, empty())
            try {
                dao.transaction {
                    dao.invalidate(Compound::class.java)
                    assertThat(dao.invalidatedClasses, empty())
                    throw RuntimeException("expected")
                }
            } catch (e: RuntimeException) {
                assertEquals("expected", e.message)
            }
            assertThat(dao.invalidatedClasses, empty())
        }
        assertThat(dao.invalidatedClasses, empty())
    }

    private class TestDao(helper: SQLiteOpenHelper) : AbstractDao(helper) {
        val invalidatedClasses = mutableListOf<Class<*>>()
        override fun onInvalidateClass(clazz: Class<*>) {
            invalidatedClasses += clazz
        }

        fun invalidate(clazz: Class<*>) = invalidateClass(clazz)
    }

    private fun SQLiteDatabase.mockTransactions() {
        val listeners = Stack<SQLiteTransactionListener?>()
        val successful = Stack<Boolean>()
        val childFailed = Stack<Boolean>()

        whenever(beginTransactionWithListener(any())) doAnswer {
            listeners.push(it.arguments[0] as? SQLiteTransactionListener)
            successful.push(false)
            childFailed.push(false)
            listeners.peek()?.onBegin()
        }

        whenever(setTransactionSuccessful()) doAnswer {
            successful.pop()
            successful.push(true)
            Unit
        }

        whenever(endTransaction()) doAnswer {
            // a transaction is successful if setTransactionSuccessful() is called and no child transactions failed
            val success = successful.pop()
            val failedChild = childFailed.pop()
            if (success && !failedChild) {
                listeners.pop()?.onCommit()
            } else {
                // indicate that the child transaction failed
                if (childFailed.isNotEmpty()) {
                    childFailed.pop()
                    childFailed.push(true)
                }
                listeners.pop()?.onRollback()
            }
        }
    }
}
