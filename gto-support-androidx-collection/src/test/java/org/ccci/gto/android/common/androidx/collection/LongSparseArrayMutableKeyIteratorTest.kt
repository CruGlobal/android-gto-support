package org.ccci.gto.android.common.androidx.collection

import androidx.collection.LongSparseArray
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Before
import org.junit.Test

class LongSparseArrayMutableKeyIteratorTest {
    val array = LongSparseArray<Boolean>()

    @Before
    fun setupArray() {
        array.put(1L, true)
        array.put(7L, true)
        array.put(2L, false)
    }

    @Test
    fun testMutableKeyIterator() {
        assertThat(array.mutableKeyIterator().asSequence().toList(), contains(1L, 2L, 7L))
    }

    @Test
    fun testMutableKeyIteratorModify() {
        val i = array.mutableKeyIterator()
        while (i.hasNext()) if (array[i.next()]!!) i.remove()

        assertThat(array.mutableKeyIterator().asSequence().toList(), contains(2L))
    }
}
