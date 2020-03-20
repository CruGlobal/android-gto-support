package org.ccci.gto.android.common.androidx.lifecycle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class EmptyLiveDataTests {
    private val missing: LiveData<String>? = null
    private val present: LiveData<String> = MutableLiveData("a")

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOrEmpty() {
        assertNull(missing.orEmpty().value)
        assertEquals("a", present.orEmpty().value)
    }

    @Test
    fun testOrEmptyInTransformation() {
        assertNull(present.switchMap { missing.orEmpty() }.value)
    }
}
