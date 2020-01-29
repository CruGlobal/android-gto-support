package org.ccci.gto.android.common.db

import android.text.TextUtils
import android.util.Pair
import org.ccci.gto.android.common.db.Contract.RootTable
import org.ccci.gto.android.common.db.Query.Companion.select
import org.ccci.gto.android.common.testing.CommonMocks
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Pair::class, TextUtils::class)
class QueryTest {
    private lateinit var dao: AbstractDao

    @Before
    @Throws(Exception::class)
    fun setup() {
        CommonMocks.mockPair()
        CommonMocks.mockTextUtils()
        dao = Mockito.mock(AbstractDao::class.java)
    }

    @Test
    @Ignore("We cannot mock dao.getTable() currently")
    fun testHavingSql() {
        // whenever(dao.getTable(eq(Root::class.java))).thenReturn(RootTable.TABLE_NAME)
        val having: Expression = RootTable.FIELD_TEST.count().eq(1)
        val query = select(RootTable::class.java).groupBy(RootTable.FIELD_TEST).having(having)
        val sqlPair = query.buildSqlHaving(dao)
        assertThat(sqlPair.first, `is`("(COUNT (root.test) == 1)"))
    }

    @Test
    fun verifyLimitSql() {
        val query = select(RootTable::class.java)
        assertThat<Any?>(query.limit(null).sqlLimit, nullValue())
        assertThat<Any?>(query.limit(null).offset(10).sqlLimit, nullValue())
        assertThat(query.limit(5).offset(null).sqlLimit, `is`("5"))
        assertThat(query.limit(5).offset(15).sqlLimit, AnyOf.anyOf(`is`("5 OFFSET 15"), `is`("15, 5")))
    }
}
