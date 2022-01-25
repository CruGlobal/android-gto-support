package org.ccci.gto.android.common.kotlin.coroutines

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadWriteMutexTest {
    private val mutex = ReadWriteMutex()

    @Test
    fun testWriteExclusion() {
        runBlocking {
            launch {
                expect(3)
                mutex.write.withLock {
                    expect(5)
                }
            }

            expect(1)
            mutex.write.withLock {
                expect(2)
                yield()
                expect(4)
            }
        }
        finish(6)
    }

    @Test
    fun testReadShared() {
        runBlocking {
            launch {
                expect(3)
                mutex.read.withLock {
                    expect(4)
                    yield()
                    expect(6)
                }
            }

            expect(1)
            mutex.read.withLock {
                expect(2)
                yield()
                expect(5)
            }
        }
        finish(7)
    }

    @Test
    fun testWriteLocksRead() {
        runBlocking {
            launch {
                mutex.read.withLock {
                    expect(2)
                }
            }

            mutex.write.withLock {
                yield()
                expect(1)
            }
        }
        finish(3)
    }

    @Test
    fun testReadsLockWrite() {
        val owner1 = Any()
        val owner2 = Any()
        val owner3 = Any()
        runBlocking {
            launch {
                expect(3)
                mutex.write.withLock(owner1) {
                    expect(8)
                }
            }

            launch {
                expect(4)
                mutex.read.withLock(owner2) {
                    expect(5)
                    yield()
                    expect(7)
                }
            }

            expect(1)
            mutex.read.withLock(owner3) {
                expect(2)
                yield()
                expect(6)
            }
        }
        finish(9)
    }

    @Test
    fun testReaderReentrancy() {
        val owner = Any()
        runBlocking {
            expect(1)
            mutex.read.withLock(owner) {
                expect(2)
                mutex.read.withLock(owner) {
                    expect(3)
                }
                expect(4)
            }
            expect(5)

            mutex.write.withLock(owner) {
                expect(6)
            }
        }
        finish(7)
    }

    @Test(expected = IllegalStateException::class)
    fun testReadLockTooManyTimes() {
        runBlocking {
            (mutex as ReadWriteMutexImpl).readers.set(Long.MAX_VALUE)
            mutex.read.lock()
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testInvalidReadUnlock() {
        mutex.read.unlock()
    }

    @Test(timeout = 10000)
    fun `GT-1423 readUnlock causes deadlock from runBlocking usage`() = runBlocking {
        mutex.write.lock()

        launch(Dispatchers.Unconfined) {
            expect(1)
            mutex.read.lock()
            assertTrue(mutex.write.isLocked)
            assertEquals(1, (mutex as ReadWriteMutexImpl).readers.get())
            expect(4)
            mutex.read.unlock()
            assertEquals(0, mutex.readers.get())
        }
        expect(2)

        assertThrows(IllegalStateException::class.java) {
            // this can cause a deadlock when runBlocking is being used
            mutex.read.unlock()
        }
        assertEquals(0, (mutex as ReadWriteMutexImpl).readers.get())
        expect(3)

        mutex.write.unlock()
    }

    @Test
    fun testInvalidReadUnlockCounterRaceCondition() {
        repeat(10) {
            runBlocking {
                val running = AtomicBoolean(true)
                val tasks = List(16) {
                    launch(Dispatchers.IO) {
                        while (running.get()) {
                            try {
                                mutex.read.unlock()
                            } catch (_: IllegalStateException) {
                            }
                        }
                        // try unlocking one last time after stopping the loop to avoid a race condition
                        try {
                            mutex.read.unlock()
                        } catch (_: IllegalStateException) {
                        }
                    }
                }
                mutex.read.lock()
                running.set(false)
                tasks.joinAll()
                assertEquals(0, (mutex as ReadWriteMutexImpl).readers.get())
            }
        }
    }

    private var actionIndex = 0
    private var finished = false
    private fun expect(index: Int) {
        val wasIndex = ++actionIndex
        check(index == wasIndex) { "Expecting action index $index but it is actually $wasIndex" }
    }

    private fun finish(index: Int) {
        expect(index)
        assertFalse("Should call 'finish(...)' at most once", finished)
        finished = true
    }
}
