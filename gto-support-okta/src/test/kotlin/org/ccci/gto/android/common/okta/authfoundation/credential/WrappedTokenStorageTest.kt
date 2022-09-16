package org.ccci.gto.android.common.okta.authfoundation.credential

import com.okta.authfoundation.credential.TokenStorage
import io.mockk.Called
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private val ID = UUID.randomUUID().toString()

@OptIn(ExperimentalCoroutinesApi::class)
class WrappedTokenStorageTest {
    private val underlyingStorage = mockk<TokenStorage>(relaxed = true)
    private val observer = mockk<TokenStorageObserverRegistry.Observer>(relaxUnitFun = true)

    private val storage = WrappedTokenStorage(underlyingStorage)

    @Test
    fun verifyAddAndRemoveObservers() {
        storage.notifyChanged(ID)
        verify { observer wasNot Called }

        storage.addObserver(observer)
        storage.notifyChanged(ID)
        verifyAll { observer.onChanged(ID) }
        clearMocks(observer)

        storage.removeObserver(observer)
        storage.notifyChanged(ID)
        verify(exactly = 0) { observer.onChanged(any()) }
    }

    @Test
    fun verifyEntriesDoesntTriggerChange() = runTest {
        val entry = mockk<TokenStorage.Entry>()

        coEvery { underlyingStorage.entries() } returns listOf(entry)
        storage.addObserver(observer)

        assertEquals(listOf(entry), storage.entries())
        coVerifyAll {
            underlyingStorage.entries()
            observer wasNot Called
        }
    }

    @Test
    fun verifyAddTriggersChange() = runTest {
        storage.addObserver(observer)

        storage.add(ID)
        coVerifyAll {
            underlyingStorage.add(ID)
            observer.onChanged(ID)
        }
    }

    @Test
    fun verifyReplaceTriggersChange() = runTest {
        val entry = TokenStorage.Entry(ID, null, emptyMap())
        storage.addObserver(observer)

        storage.replace(entry)
        coVerifyAll {
            underlyingStorage.replace(entry)
            observer.onChanged(ID)
        }
    }

    @Test
    fun verifyRemoveTriggersChange() = runTest {
        storage.addObserver(observer)

        storage.remove(ID)
        coVerifyAll {
            underlyingStorage.remove(ID)
            observer.onChanged(ID)
        }
    }
}
