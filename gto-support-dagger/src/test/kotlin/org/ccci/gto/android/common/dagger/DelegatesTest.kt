package org.ccci.gto.android.common.dagger

import dagger.Lazy
import javax.inject.Provider
import org.junit.Assert.assertEquals
import org.junit.Test

private const val VALUE = "value"

class DelegatesTest {
    @Test
    fun testLazyDelegate() {
        val lazy = Lazy { VALUE }
        val delegate by lazy
        assertEquals(VALUE, delegate)
    }

    @Test
    fun testProviderDelegate() {
        val provider = Provider { VALUE }
        val delegate by provider
        assertEquals(VALUE, delegate)
    }
}
