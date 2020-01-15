package org.ccci.gto.android.common.db

import org.ccci.gto.android.common.db.Contract.RootTable
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class AbstractDaoTests {
    @Test
    fun testPrefixOrderBySingleField() {
        val prefix = RootTable.TABLE_NAME + "."
        val editedClause = RootTable.COLUMN_ID.prefixOrderByFieldsWith(prefix)
        assertThat(editedClause, CoreMatchers.`is`("root._id"))
    }

    @Test
    fun testPrefixOrderByMultipleFields() {
        val prefix = RootTable.TABLE_NAME + "."
        val editedClause = "${RootTable.COLUMN_ID},${RootTable.COLUMN_TEST}".prefixOrderByFieldsWith(prefix)
        assertThat(editedClause, CoreMatchers.`is`("root._id,root.test"))
    }

    @Test
    fun testPrefixOrderByMultipleFieldsSomePrefixed() {
        val prefix = RootTable.TABLE_NAME + "."
        val editedClause = "a.${RootTable.COLUMN_ID},${RootTable.COLUMN_TEST}".prefixOrderByFieldsWith(prefix)
        assertThat(editedClause, CoreMatchers.`is`("a._id,root.test"))
    }

    @Test
    fun testPrefixOrderByMultipleFieldsAllPrefixed() {
        val prefix = RootTable.TABLE_NAME + "."
        val editedClause =
            "a.${RootTable.COLUMN_ID},b.${RootTable.COLUMN_TEST}".prefixOrderByFieldsWith(prefix)
        assertThat(editedClause, CoreMatchers.`is`("a._id,b.test"))
    }
}
