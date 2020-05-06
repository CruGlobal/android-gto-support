package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.model.Model1
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

private const val TABLE_NAME = "root"
private const val FIELD_NAME = "test"
private val FIELD = Table.forClass(Model1::class.java).field(FIELD_NAME)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class QueryTest {
    @Test
    fun testHavingSql() {
        val dao: AbstractDao = mock()
        whenever(dao.tableName(eq(Model1::class.java))).thenReturn(TABLE_NAME)
        val query = Query.select(Model1::class.java).groupBy(FIELD).having(FIELD.count().eq(1))
        assertEquals("(COUNT ($TABLE_NAME.$FIELD_NAME) == 1)", query.buildSqlHaving(dao).first)
    }

    @Test
    fun verifyLimitSql() {
        val query = Query.select(Model1::class.java)
        assertNull(query.limit(null).sqlLimit)
        assertNull(query.offset(10).limit(null).sqlLimit)
        assertEquals("5", query.limit(5).offset(null).sqlLimit)
        assertThat(query.limit(5).offset(15).sqlLimit, anyOf(equalTo("5 OFFSET 15"), equalTo("15, 5")))
    }
}
