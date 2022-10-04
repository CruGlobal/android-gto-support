package org.ccci.gto.android.common.kotlin.coroutines.flow

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombineTransformLatestTest {
    @Test
    fun testCombineTransformLatest() = runTest(dispatchTimeoutMs = 1_000) {
        val flow1 = MutableStateFlow(0)
        val flow2 = MutableStateFlow(0)
        val innerFlow = MutableSharedFlow<Int>()

        combineTransformLatest(flow1, flow2) { it1, it2 ->
            innerFlow.take(3).collect { emit(it1 + it2 + it) }
        }.buffer(1).test {
            innerFlow.emit(0)
            assertEquals(0, awaitItem())
            innerFlow.emit(1)
            assertEquals(1, awaitItem())

            // flow1 emitting should cancel & restart transform
            flow1.value = 100
            innerFlow.emit(0)
            assertEquals(100, awaitItem())
            innerFlow.emit(1)
            assertEquals(101, awaitItem())

            // flow2 emitting should also cancel & restart transform
            flow2.value = 20
            innerFlow.emit(0)
            assertEquals(120, awaitItem())
            innerFlow.emit(1)
            assertEquals(121, awaitItem())
            innerFlow.emit(2)
            assertEquals(122, awaitItem())

            // transform should have run to completion
            innerFlow.emit(3)
            ensureAllEventsConsumed()

            // new emission restarts transform
            flow2.value = 30
            innerFlow.emit(0)
            assertEquals(130, awaitItem())
        }
    }
}
