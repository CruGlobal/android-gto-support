package org.ccci.gto.android.common.db

import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class CoroutinesFlowDaoTest {
    private val callback = slot<Dao.InvalidationCallback>()
    private val dao = spyk<CoroutinesFlowDao> {
        every { services } returns mutableMapOf()
        every { registerInvalidationCallback(capture(callback)) } returns Unit
    }

    @Test
    fun verifyFindAsFlowMonitorsInvalidations() = runTest(UnconfinedTestDispatcher()) {
        every { dao.coroutineDispatcher } returns coroutineContext[CoroutineDispatcher]!!
        val job = dao.findAsFlow(String::class.java).launchIn(this)
        verify { dao.registerInvalidationCallback(any()) }
        verify(inverse = true) { dao.unregisterInvalidationCallback(any()) }

        job.cancel()
        verify { dao.unregisterInvalidationCallback(any()) }
    }

    @Test
    fun verifyFindAsFlow() = runTest {
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
