package org.ccci.gto.android.common.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.ccci.gto.android.common.db.Contract.RootTable
import org.ccci.gto.android.common.db.model.Root
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ExpressionTest {
    private lateinit var dao: AbstractDao

    @Before
    @Throws(Exception::class)
    fun setup() {
        dao = mock()
        whenever(dao.tableName(eq(Root::class.java))).thenReturn(RootTable.TABLE_NAME)
    }

    @Test
    fun testEqualsSql() {
        val equalsExpression: Expression = RootTable.FIELD_TEST.eq("1")
        assertThat(equalsExpression.buildSql(dao).first, equalTo("(root.test == ?)"))
    }

    @Test
    fun testNotEqualsSql() {
        val notEqualsExpression: Expression = RootTable.FIELD_TEST.ne("1")
        assertThat(notEqualsExpression.buildSql(dao).first, equalTo("(root.test != ?)"))
    }

    @Test
    fun testCount() {
        val countExpression: Expression = RootTable.FIELD_TEST.count()
        assertThat(countExpression.buildSql(dao).first, equalTo("COUNT (root.test)"))
    }

    @Test
    fun testCountInHaving() {
        val countExpression: Expression = RootTable.FIELD_TEST.count().eq(1)
        assertThat(countExpression.buildSql(dao).first, equalTo("(COUNT (root.test) == 1)"))
    }
}
