package org.ccci.gto.android.common.db

import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class CoroutinesFlowDaoTest {
    private val callback = slot<Dao.InvalidationCallback>()
    private val dao = spyk<CoroutinesFlowDao> {
        every { services } returns mutableMapOf()
        every { registerInvalidationCallback(capture(callback)) } returns Unit
    }

    @Test
    fun `invalidationFlow() - emitOnStart=true`() = runTest {
        every { dao.coroutineDispatcher } returns coroutineContext[CoroutineDispatcher]!!
        val output = ArrayDeque<Unit>()
        val job = dao.invalidationFlow(String::class.java, emitOnStart = true)
            .onEach {
                assertTrue(
                    "Invalidations should only emit if we are actively monitoring for additional invalidations",
                    callback.isCaptured
                )
            }
            .onEach { output.add(it) }
            .launchIn(this)

        // initial emit
        advanceUntilIdle()
        verify { dao.registerInvalidationCallback(any()) }
        verify(inverse = true) { dao.unregisterInvalidationCallback(any()) }
        output.removeFirst()
        assertTrue(output.isEmpty())

        // invalidation triggers flow emission
        callback.captured.onInvalidate(String::class.java)
        advanceUntilIdle()
        output.removeFirst()
        assertTrue(output.isEmpty())

        // invalidation of a class not being monitored isn't emitted
        callback.captured.onInvalidate(Float::class.java)
        advanceUntilIdle()
        assertTrue(output.isEmpty())

        job.cancel()
        advanceUntilIdle()
        verify { dao.unregisterInvalidationCallback(any()) }
        assertTrue(output.isEmpty())
    }

    @Test
    fun `invalidationFlow() - emitOnStart=false`() = runTest {
        every { dao.coroutineDispatcher } returns coroutineContext[CoroutineDispatcher]!!
        val output = ArrayDeque<Unit>()
        val job = dao.invalidationFlow(String::class.java, emitOnStart = false)
            .onEach {
                assertTrue(
                    "Invalidations should only emit if we are actively monitoring for additional invalidations",
                    callback.isCaptured
                )
            }
            .onEach { output.add(it) }
            .launchIn(this)

        // initial emit
        advanceUntilIdle()
        verify { dao.registerInvalidationCallback(any()) }
        verify(inverse = true) { dao.unregisterInvalidationCallback(any()) }
        assertTrue(output.isEmpty())

        // invalidation triggers flow emission
        callback.captured.onInvalidate(String::class.java)
        advanceUntilIdle()
        output.removeFirst()
        assertTrue(output.isEmpty())

        // invalidation of a class not being monitored isn't emitted
        callback.captured.onInvalidate(Float::class.java)
        advanceUntilIdle()
        assertTrue(output.isEmpty())

        job.cancel()
        advanceUntilIdle()
        verify { dao.unregisterInvalidationCallback(any()) }
        assertTrue(output.isEmpty())
    }

    @Test
    fun `findAsFlow() - Monitors Invalidations`() = runTest(UnconfinedTestDispatcher()) {
        every { dao.coroutineDispatcher } returns coroutineContext[CoroutineDispatcher]!!
        val job = dao.findAsFlow(String::class.java).launchIn(this)
        verifyOrder {
            dao.registerInvalidationCallback(any())
            // Ensure we query data at least once after we register the invalidation callback to avoid a race condition.
            // We risk not emitting fresh data if an invalidation triggers after we last fetched data but before we
            // register the callback.
            dao.find(String::class.java)
        }
        verify(inverse = true) { dao.unregisterInvalidationCallback(any()) }

        job.cancel()
        verify { dao.unregisterInvalidationCallback(any()) }
    }

    @Test
    fun `findAsFlow()`() = runTest {
        every { dao.coroutineDispatcher } returns coroutineContext[CoroutineDispatcher]!!
        val flow = dao.findAsFlow(String::class.java).launchIn(this)
        verify(exactly = 0) { dao.find(String::class.java) }

        // initial trigger
        advanceUntilIdle()
        verify { dao.registerInvalidationCallback(any()) }
        verify(exactly = 1) { dao.find(String::class.java) }

        // invalidation triggers processing
        callback.captured.onInvalidate(String::class.java)
        advanceUntilIdle()
        verify(exactly = 2) { dao.find(String::class.java) }

        // invalidation of a class not being monitored isn't collected
        callback.captured.onInvalidate(Float::class.java)
        advanceUntilIdle()
        verify(exactly = 2) { dao.find(String::class.java) }

        flow.cancel()
    }
}
