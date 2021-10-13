package org.ccci.gto.android.common.okta.oidc.storage

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class OktaStorageChangeFlowTest {
    private lateinit var storage: ChangeAwareOktaStorage

    @Before
    fun setup() {
        storage = spy(WrappedOktaStorage(mock()))
    }

    @Test
    fun verifyChangeFlowBehavior() = runBlockingTest {
        val values = mutableListOf<Unit>()
        verify(storage, never()).addObserver(any())

        // should observe storage & emit initial value
        val job = launch { storage.changeFlow().collect { values += it } }
        verify(storage).addObserver(any())
        verify(storage, never()).removeObserver(any())
        assertEquals(1, values.size)

        // should emit only without changing observer registration
        reset(storage)
        values.clear()
        storage.notifyChanged()
        verify(storage, never()).addObserver(any())
        verify(storage, never()).removeObserver(any())
        assertEquals(1, values.size)

        // should remove subscriber
        reset(storage)
        values.clear()
        job.cancel()
        verify(storage, never()).addObserver(any())
        verify(storage).removeObserver(any())
        assertEquals(0, values.size)
    }
}
