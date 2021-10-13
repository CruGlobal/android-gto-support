package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.verify

class TransformationsFoldTest : BaseLiveDataTest() {
    @Test
    fun verifyLiveDataSwitchFold() {
        val source = MutableLiveData(listOf(0, 1))
        val data = listOf(MutableLiveData("a"), MutableLiveData("b"), MutableLiveData("c"), MutableLiveData())

        val liveData = source.switchFold { acc: LiveData<String?>, i ->
            acc.switchCombineWith(data[i]) { a, b -> MutableLiveData(a.orEmpty() + b.orEmpty()) }
        }
        liveData.observeForever(observer)
        assertEquals("ab", liveData.value)
        source.value = listOf(0, 1, 2)
        assertEquals("abc", liveData.value)
        source.value = listOf(2, 1, 2)
        assertEquals("cbc", liveData.value)
        source.value = listOf(3)
        assertEquals("cbc", liveData.value)
        data[3].value = "d"
        assertEquals("d", liveData.value)
        argumentCaptor<String> {
            verify(observer, atLeast(4)).onChanged(capture())
            assertThat(allValues, contains("ab", "abc", "cbc", "d"))
        }
    }
}
