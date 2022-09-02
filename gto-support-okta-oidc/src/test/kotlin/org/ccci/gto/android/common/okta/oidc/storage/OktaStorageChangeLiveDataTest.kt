package org.ccci.gto.android.common.okta.oidc.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

class OktaStorageChangeLiveDataTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var storage: ChangeAwareOktaStorage
    private lateinit var observer: Observer<Unit>

    private lateinit var liveData: OktaStorageChangeLiveData

    @Before
    fun setup() {
        storage = spy(WrappedOktaStorage(mock()))
        observer = mock()

        liveData = OktaStorageChangeLiveData(storage)
    }

    @Test
    fun verifyObserverBehavior() {
        verify(storage, never()).addObserver(any())

        // should observe storage & trigger Observer
        liveData.observeForever(observer)
        verify(storage).addObserver(liveData)
        verify(storage, never()).notifyChanged()
        verify(storage, never()).removeObserver(any())
        verify(observer).onChanged(any())

        // should trigger Observer only without changing observer registration
        reset(storage, observer)
        storage.notifyChanged()
        verify(storage, never()).addObserver(any())
        verify(storage, never()).removeObserver(any())
        verify(observer).onChanged(any())

        // should remove subscriber
        reset(storage, observer)
        liveData.removeObserver(observer)
        verify(storage, never()).addObserver(any())
        verify(storage).removeObserver(liveData)
    }
}
