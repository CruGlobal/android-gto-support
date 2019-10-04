package org.ccci.gto.android.common.lifecycle.databinding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.ccci.gto.android.common.lifecycle.orEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class EmptyLiveDataTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testOrEmpty() {
        val missing: LiveData<String>? = null
        val present: LiveData<String> = MutableLiveData("a")

        assertNull(missing.orEmpty().value)
        assertEquals("a", present.orEmpty().value)
    }
}
