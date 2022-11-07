package org.ccci.gto.android.common.kotlin.coroutines.flow

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombineTransformLatestTest {
    private val flow1 = MutableStateFlow(0)
    private val flow2 = MutableStateFlow(0)
    private val flow3 = MutableStateFlow(0)

    @Test
    fun `combineTransformLatest(flow1, flow2)`() = runTest {
        val innerFlow = MutableSharedFlow<Int>()

        combineTransformLatest(flow1, flow2) { it1, it2 ->
            emitAll(innerFlow.take(3).map { it1 + it2 + it })
        }.test {
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

    @Test
    fun `combineTransformLatest(flow1, flow2, flow3)`() = runTest {
        val innerFlow = MutableSharedFlow<Int>()

        combineTransformLatest(flow1, flow2, flow3) { it1, it2, it3 ->
            emitAll(innerFlow.take(3).map { it1 + it2 + it3 + it })
        }.test {
            innerFlow.emit(0)
            assertEquals(0, awaitItem())
            innerFlow.emit(1)
            assertEquals(1, awaitItem())

            // flow1 emitting should cancel & restart transform
            flow1.value = 1000
            innerFlow.emit(0)
            assertEquals(1000, awaitItem())
            innerFlow.emit(1)
            assertEquals(1001, awaitItem())

            // flow2 emitting should cancel & restart transform
            flow2.value = 200
            innerFlow.emit(0)
            assertEquals(1200, awaitItem())
            innerFlow.emit(1)
            assertEquals(1201, awaitItem())

            // flow3 emitting should cancel & restart transform
            flow3.value = 30
            innerFlow.emit(0)
            assertEquals(1230, awaitItem())
            innerFlow.emit(1)
            assertEquals(1231, awaitItem())
            innerFlow.emit(2)
            assertEquals(1232, awaitItem())

            // transform should have run to completion
            innerFlow.emit(3)
            ensureAllEventsConsumed()

            // new emission restarts transform
            flow3.value = 40
            innerFlow.emit(0)
            assertEquals(1240, awaitItem())
        }
    }
}
