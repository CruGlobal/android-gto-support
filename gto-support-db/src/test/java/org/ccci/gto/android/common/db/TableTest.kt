package org.ccci.gto.android.common.db

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.Table.Companion.forClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class TableTest {
    private lateinit var dao: AbstractDao

    @Before
    fun setup() {
        dao = mock()

        whenever(dao.tableName(eq(Obj1::class.java))).thenReturn(Obj1.TABLE_NAME)
        whenever(dao.tableName(eq(Obj2::class.java))).thenReturn(Obj2.TABLE_NAME)
    }

    @Test
    fun testSqlTable() {
        val t1 = forClass(Obj1::class.java)
        val t2 = forClass(Obj2::class.java)
        assertEquals(Obj1.TABLE_NAME, t1.sqlTable(dao))
        assertEquals(Obj1.TABLE_NAME + " AS a", t1.`as`("a").sqlTable(dao))
        assertEquals(Obj1.TABLE_NAME + " AS abcde", t1.`as`("abcde").sqlTable(dao))
        assertNotEquals(Obj2.TABLE_NAME, t1.sqlTable(dao))
        assertEquals(Obj2.TABLE_NAME, t2.sqlTable(dao))
        assertEquals(Obj2.TABLE_NAME + " AS b", t2.`as`("b").sqlTable(dao))
    }

    private object Obj1 {
        const val TABLE_NAME = "Table1"
    }

    private object Obj2 {
        const val TABLE_NAME = "Table2"
    }
}
