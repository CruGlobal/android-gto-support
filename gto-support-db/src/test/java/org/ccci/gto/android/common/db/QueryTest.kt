package org.ccci.gto.android.common.db

import android.text.TextUtils
import android.util.Pair
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.Contract.RootTable
import org.ccci.gto.android.common.db.Query.Companion.select
import org.ccci.gto.android.common.db.model.Root
import org.ccci.gto.android.common.testing.CommonMocks
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.AnyOf.anyOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Pair::class, TextUtils::class)
class QueryTest {
    @Before
    fun setup() {
        CommonMocks.mockPair()
        CommonMocks.mockTextUtils()
    }

    @Test
    fun testHavingSql() {
        val dao = Mockito.mock(AbstractDao::class.java)
        whenever(dao.tableName(eq(Root::class.java))).thenReturn(RootTable.TABLE_NAME)
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
