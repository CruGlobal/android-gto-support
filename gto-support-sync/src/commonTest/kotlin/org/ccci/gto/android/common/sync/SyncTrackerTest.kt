package org.ccci.gto.android.common.sync

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SyncTrackerTest {
    private val testScope = TestScope()

    private val tracker = SyncTracker(testScope.backgroundScope)

    // region Property isInitialSyncFinished
    @Test
    fun `Property isInitialSyncFinished`() = testScope.runTest {
        tracker.isInitialSyncFinished.test {
            assertFalse(awaitItem())

            tracker.runSync { }
            assertTrue(awaitItem())
        }
    }
    // endregion Property isInitialSyncFinished

    // region Property isSyncing
    @Test
    fun `Property isSyncing - true while syncs are running`() = testScope.runTest {
        val semaphore = Semaphore(1)

        tracker.isSyncing.test {
            assertFalse(awaitItem())

            semaphore.acquire()
            tracker.launchSync { semaphore.acquire() }
            assertTrue(awaitItem())
            runCurrent()
            expectNoEvents()

            semaphore.release()
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `Property isSyncing - Concurrent tasks maintain proper state`() = testScope.runTest {
        val semaphore = Semaphore(1)

        tracker.isSyncing.test {
            assertFalse(awaitItem())

            semaphore.acquire()
            tracker.launchSync { semaphore.acquire() }
            assertTrue(awaitItem())
            runCurrent()
            expectNoEvents()

            tracker.runSync { }
            runCurrent()
            expectNoEvents()

            semaphore.release()
            assertFalse(awaitItem())
        }
    }
    // endregion Property isSyncing

    // region runSync()
    @Test
    fun `runSync - runs provided block`() = testScope.runTest {
        var syncRan = false
        tracker.runSync { syncRan = true }
        assertTrue(syncRan)
    }
    // endregion runSync()

    // region launchSync()
    @Test
    fun `launchSync - runs provided block`() = testScope.runTest {
        val semaphore = Semaphore(1)
        var syncRan = false

        semaphore.acquire()
        tracker.launchSync {
            syncRan = true
            semaphore.release()
        }

        semaphore.acquire()
        assertTrue(syncRan)
    }
    // endregion launchSync()
}
