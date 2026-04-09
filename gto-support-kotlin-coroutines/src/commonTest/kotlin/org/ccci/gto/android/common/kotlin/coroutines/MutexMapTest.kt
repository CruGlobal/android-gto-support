package org.ccci.gto.android.common.kotlin.coroutines

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield

class MutexMapTest {
    private val map = MutexMap()

    // region withLock
    @Test
    fun `withLock - executes action and returns result`() = runTest {
        assertEquals(42, map.withLock("key") { 42 })
    }

    @Test
    fun `withLock - provides mutual exclusion for the same key`() = runTest {
        val semaphore = Semaphore(1)
        List(100) {
            launch {
                map.withLock("key") {
                    if (!semaphore.tryAcquire()) fail("Concurrent execution detected for key")
                    delay(1_000)
                    semaphore.release()
                }
            }
        }
    }

    @Test
    fun `withLock - allows concurrent execution for different keys`() = runTest(timeout = 10.seconds) {
        val semaphore1 = Semaphore(1, 1)
        val semaphore2 = Semaphore(1, 1)

        val job1 = launch {
            map.withLock("key1") {
                semaphore2.release()
                semaphore1.acquire()
            }
        }

        val job2 = launch {
            map.withLock("key2") {
                semaphore1.release()
                semaphore2.acquire()
            }
        }

        job1.join()
        job2.join()
    }
    // endregion withLock
}
