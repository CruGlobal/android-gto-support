package org.ccci.gto.android.common.db

import org.ccci.gto.android.common.db.Contract.RootTable
import org.ccci.gto.android.common.db.Query.Companion.select
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito

class QueryTest {
    @Test
    @Ignore("We cannot mock dao.getTable() currently, this will probably change as we convert more code to Kotlin")
    fun testHavingSql() {
        val dao = Mockito.mock(AbstractDao::class.java)
        // whenever(dao.getTable(eq(Root::class.java))).thenReturn(RootTable.TABLE_NAME)
        val having: Expression = RootTable.FIELD_TEST.count().eq(1)
        val query = select(RootTable::class.java).groupBy(RootTable.FIELD_TEST).having(having)
        val sqlPair = query.buildSqlHaving(dao)
        assertThat(sqlPair.first, equalTo("(COUNT (root.test) == 1)"))
    }

    @Test
    fun verifyLimitSql() {
        val query = select(RootTable::class.java)
        assertThat<Any?>(query.limit(null).sqlLimit, nullValue())
        assertThat<Any?>(query.limit(null).offset(10).sqlLimit, nullValue())
        assertThat(query.limit(5).offset(null).sqlLimit, equalTo("5"))
        assertThat(query.limit(5).offset(15).sqlLimit, anyOf(equalTo("5 OFFSET 15"), equalTo("15, 5")))
    }
}
