package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.model.Model1
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val TABLE_NAME = "root"
private const val FIELD_NAME = "test"
private val FIELD = Table.forClass(Model1::class.java).field("test")

@RunWith(AndroidJUnit4::class)
class ExpressionTest {
    private lateinit var dao: AbstractDao

    @Before
    fun setup() {
        dao = mock()
        whenever(dao.tableName(eq(Model1::class.java))).thenReturn(TABLE_NAME)
    }

    @Test
    fun testEqualsSql() {
        assertEquals("($TABLE_NAME.$FIELD_NAME == ?)", FIELD.eq("1").buildSql(dao).first)
    }

    @Test
    fun testNotEqualsSql() {
        assertEquals("($TABLE_NAME.$FIELD_NAME != ?)", FIELD.ne("1").buildSql(dao).first)
    }

    @Test
    fun testCount() {
        assertEquals("COUNT ($TABLE_NAME.$FIELD_NAME)", FIELD.count().buildSql(dao).first)
    }

    @Test
    fun testCountInHaving() {
        assertEquals("(COUNT ($TABLE_NAME.$FIELD_NAME) == 1)", FIELD.count().eq(1).buildSql(dao).first)
    }
}
