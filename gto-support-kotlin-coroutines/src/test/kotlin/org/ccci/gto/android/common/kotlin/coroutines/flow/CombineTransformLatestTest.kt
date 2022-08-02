package org.ccci.gto.android.common.kotlin.coroutines.flow

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombineTransformLatestTest {
    @Test
    fun testCombineTransformLatest() = runTest(dispatchTimeoutMs = 1000) {
        val flow1 = MutableStateFlow(0)
        val flow2 = MutableStateFlow(0)

        combineTransformLatest(flow1, flow2) { it1, it2 ->
            (0..2).forEach {
                emit(it1 + it2 + it)
                delay(1)
            }
        }.test {
            assertEquals(0, awaitItem())
            assertEquals(1, awaitItem())

            // flow1 emitting should cancel & restart transform
            flow1.value = 100
            assertEquals(100, awaitItem())
            assertEquals(101, awaitItem())

            // flow2 emitting should also cancel & restart transform
            flow2.value = 20
            assertEquals(120, awaitItem())
            assertEquals(121, awaitItem())
            assertEquals(122, awaitItem())

            // transform should have run to completion
            delay(100)
            ensureAllEventsConsumed()

            // new emission restarts transform
            flow2.value = 30
            assertEquals(130, awaitItem())
            cancel()
        }
    }
}
