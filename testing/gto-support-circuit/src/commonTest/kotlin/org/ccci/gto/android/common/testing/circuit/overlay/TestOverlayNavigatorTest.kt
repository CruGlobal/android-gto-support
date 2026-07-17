package org.ccci.gto.android.common.testing.circuit.overlay

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest

class TestOverlayNavigatorTest {
    @Test
    fun `awaitResult - returns the finished result`() = runTest {
        val navigator = TestOverlayNavigator<String>()

        navigator.finish("hello")

        assertEquals("hello", navigator.awaitResult())
    }

    @Test
    fun `finish - throws when called more than once`() {
        val navigator = TestOverlayNavigator<String>()
        navigator.finish("first")

        assertFailsWith<IllegalArgumentException> { navigator.finish("second") }
    }
}
