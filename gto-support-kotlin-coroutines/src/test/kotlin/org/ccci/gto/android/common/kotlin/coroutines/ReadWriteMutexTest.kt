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
            (mutex as ReadWriteMutexImpl).readers = Long.MAX_VALUE - 1
            while (true) mutex.read.lock()
        }
    }

    @Test(expected = IllegalStateException::class)
    fun testInvalidReadUnlock() {
        runBlocking {
            mutex.read.unlock()
        }
    }

    @Test
    fun testInvalidReadUnlockCounterRaceCondition() {
        repeat(10) {
            runBlocking {
                val running = AtomicBoolean(true)
                val tasks = List(16) {
                    launch(Dispatchers.IO) {
                        do {
                            try {
                                mutex.read.unlock()
                            } catch (_: IllegalStateException) {
                            }
                        } while (running.get())
                    }
                }
                mutex.read.lock()
                running.set(false)
                tasks.joinAll()
                assertEquals(0, (mutex as ReadWriteMutexImpl).readers)
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
