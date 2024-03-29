package org.ccci.gto.android.common.okta.oidc.storage

import com.okta.oidc.storage.OktaStorage
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

private const val KEY = "key"
private const val VALUE = "value"

class WrappedOktaStorageTest {
    private lateinit var underlyingStorage: OktaStorage
    private lateinit var observer: ChangeAwareOktaStorage.Observer

    private lateinit var storage: WrappedOktaStorage

    @Before
    fun setup() {
        underlyingStorage = mock()
        observer = mock()
        storage = WrappedOktaStorage(underlyingStorage)
    }

    @Test
    fun verifyAddAndRemoveObservers() {
        storage.notifyChanged()
        verify(observer, never()).onChanged()

        storage.addObserver(observer)
        storage.notifyChanged()
        verify(observer).onChanged()

        reset(observer)
        storage.removeObserver(observer)
        storage.notifyChanged()
        verify(observer, never()).onChanged()
    }

    @Test
    fun verifySaveTriggersChange() {
        storage.addObserver(observer)

        storage.save(KEY, VALUE)
        verify(underlyingStorage).save(KEY, VALUE)
        verify(observer).onChanged()
    }

    @Test
    fun verifyGetDoesntTriggerChange() {
        storage.addObserver(observer)

        storage.get(KEY)
        verify(underlyingStorage).get(KEY)
        verify(observer, never()).onChanged()
    }

    @Test
    fun verifyDeleteTriggersChange() {
        storage.addObserver(observer)

        storage.delete(KEY)
        verify(underlyingStorage).delete(KEY)
        verify(observer).onChanged()
    }
}
