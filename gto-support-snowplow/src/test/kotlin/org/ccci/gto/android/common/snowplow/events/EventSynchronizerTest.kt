package org.ccci.gto.android.common.snowplow.events

import com.snowplowanalytics.snowplow.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

private const val THREADS = 50
private const val ITERATIONS = 50

private const val TIMEOUT_TEST_LOCK_TIMEOUT = 1L
private const val TIMEOUT_TEST_DELAY = TIMEOUT_TEST_LOCK_TIMEOUT * 3

class EventSynchronizerTest {
    @Test
    fun verifyMutualExclusionOfLock() = runBlocking {
        var count = 0
        val mutex = Mutex()
        coroutineScope {
            repeat(THREADS) {
                launch(Dispatchers.IO) {
                    repeat(ITERATIONS) {
                        val event = mock<Event>()
                        EventSynchronizer.lockFor(event)
                        assertEquals(0, EventSynchronizer.semaphore.availablePermits())
                        assertTrue("Mutual Exclusion not maintained!", mutex.tryLock(this))
                        mutex.unlock(this)
                        count++
                        EventSynchronizer.unlockFor(event)
                        assertTrue(EventSynchronizer.semaphore.availablePermits() <= 1)
                    }
                }
            }
        }

        assertEquals(1, EventSynchronizer.semaphore.availablePermits())
        assertEquals(THREADS * ITERATIONS, count)
    }

    @Test(timeout = THREADS * ITERATIONS * TIMEOUT_TEST_DELAY)
    fun verifyLockTimeout() = runBlocking {
        var count = 0
        EventSynchronizer.lockTimeout = TIMEOUT_TEST_LOCK_TIMEOUT

        coroutineScope {
            repeat(THREADS) {
                launch(Dispatchers.IO) {
                    repeat(ITERATIONS) {
                        val event = mock<Event>()
                        EventSynchronizer.lockFor(event)
                        assertEquals(
                            "EventSynchronizer is not currently locked",
                            0,
                            EventSynchronizer.semaphore.availablePermits()
                        )
                        count++
                        delay(TIMEOUT_TEST_DELAY)
                        EventSynchronizer.unlockFor(event)
                        assertTrue(
                            "There are too many permits in the semaphore",
                            EventSynchronizer.semaphore.availablePermits() <= 1
                        )
                    }
                }
            }
        }

        assertEquals(1, EventSynchronizer.semaphore.availablePermits())
        assertEquals("Not all iterations were processed", THREADS * ITERATIONS, count)
    }
}
