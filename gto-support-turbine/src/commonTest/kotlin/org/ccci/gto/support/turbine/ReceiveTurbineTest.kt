package org.ccci.gto.support.turbine

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class ReceiveTurbineTest {
    @Test
    fun `awaitItemMatching() - discard items that don't match the predicate`() = runTest {
        flowOf(1, 2, 3).test {
            assertEquals(2, awaitItemMatching { it == 2 })
            assertEquals(3, awaitItem())
            awaitComplete()
        }
    }
}
