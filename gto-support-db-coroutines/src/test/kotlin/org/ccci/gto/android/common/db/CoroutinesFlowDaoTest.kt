package org.ccci.gto.android.common.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class CoroutinesFlowDaoTest {
    private val dao = spy<CoroutinesFlowDao> {
        on { services } doReturn mutableMapOf()
    }

    @Test
    fun verifyFindAsFlowMonitorsInvalidations() = runTest(UnconfinedTestDispatcher()) {
        whenever(dao.coroutineDispatcher) doReturn coroutineContext[CoroutineDispatcher]!!
        val job = dao.findAsFlow(String::class.java).launchIn(this)
        val callback = verifyInvalidationCallback()
        verify(dao, never()).unregisterInvalidationCallback(any())

        job.cancel()
        verify(dao).unregisterInvalidationCallback(callback)
    }

    @Test
    fun verifyFindAsFlow() = runTest {
        whenever(dao.coroutineDispatcher) doReturn coroutineContext[CoroutineDispatcher]!!
        val flow = dao.findAsFlow(String::class.java).launchIn(this)
        verify(dao, never()).find(String::class.java)

        // initial trigger
        advanceUntilIdle()
        val callback = verifyInvalidationCallback()
        verify(dao).find(String::class.java)
        clearInvocations(dao)

        // invalidation triggers processing
        callback.onInvalidate(String::class.java)
        advanceUntilIdle()
        verify(dao).find(String::class.java)
        clearInvocations(dao)

        // invalidation of a class not being monitored isn't collected
        callback.onInvalidate(Float::class.java)
        advanceUntilIdle()
        verify(dao, never()).find(String::class.java)

        flow.cancel()
    }

    private fun verifyInvalidationCallback() =
        argumentCaptor<Dao.InvalidationCallback> { verify(dao).registerInvalidationCallback(capture()) }.firstValue
}
