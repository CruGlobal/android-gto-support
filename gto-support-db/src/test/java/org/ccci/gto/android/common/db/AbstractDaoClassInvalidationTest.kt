package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteTransactionListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.whenever
import java.util.Stack
import org.ccci.gto.android.common.db.model.Model1
import org.ccci.gto.android.common.db.model.Model2
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AbstractDaoClassInvalidationTest : BaseAbstractDaoTest() {
    @Before
    fun setupDao() {
        db.mockTransactions()
    }

    @Test
    fun verifyInvalidateClassWithoutTransaction() {
        dao.invalidate(Model1::class.java)
        assertThat(dao.invalidatedClasses, contains<Class<*>>(Model1::class.java))
    }

    @Test
    fun verifyInvalidateClassWithSingleTransaction() {
        dao.transaction {
            dao.invalidate(Model1::class.java)
            assertThat(dao.invalidatedClasses, empty())
        }
        assertThat(dao.invalidatedClasses, contains<Class<*>>(Model1::class.java))
    }

    @Test
    fun verifyUnsuccessfulInvalidateClassWithSingleTransaction() {
        try {
            dao.transaction {
                dao.invalidate(Model1::class.java)
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
            dao.invalidate(Model1::class.java)
            assertThat(dao.invalidatedClasses, empty())
            try {
                dao.transaction {
                    dao.invalidate(Model2::class.java)
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
