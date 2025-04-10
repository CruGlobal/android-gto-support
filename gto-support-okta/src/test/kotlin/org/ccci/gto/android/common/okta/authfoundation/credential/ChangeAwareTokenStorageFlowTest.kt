package org.ccci.gto.android.common.okta.authfoundation.credential

import app.cash.turbine.test
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangeAwareTokenStorageFlowTest {
    private val storage: ChangeAwareTokenStorage = spyk(WrappedTokenStorage(mockk(relaxUnitFun = true))) {
        excludeRecords { notifyChanged(any()) }
    }

    @Test
    fun verifyChangeFlowBehavior() = runTest(UnconfinedTestDispatcher()) {
        val flow = storage.changeFlow()
        verify { storage wasNot Called }

        flow.test {
            // we should emit an initial item
            awaitItem()
            expectNoEvents()
            verify(exactly = 1) { storage.addObserver(any()) }
            confirmVerified(storage)

            // emit on notify without changing observer registration
            storage.notifyChanged(UUID.randomUUID().toString())
            awaitItem()
            expectNoEvents()
            confirmVerified(storage)

            // cancelling the flow should remove the subscriber
            cancel()
            verify(exactly = 1) { storage.removeObserver(any()) }
            confirmVerified(storage)
        }
    }
}
