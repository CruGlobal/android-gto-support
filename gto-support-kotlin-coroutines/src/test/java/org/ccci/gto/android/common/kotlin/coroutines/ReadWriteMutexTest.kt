package org.ccci.gto.android.common.kotlin.coroutines

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
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
                mutex.withReadLock {
                    yield()
                    expect(2)
                    yield()
                    expect(4)
                }
            }

            launch {
                mutex.withReadLock {
                    expect(1)
                    yield()
                    expect(3)
                }
            }
        }
        finish(5)
    }

    @Test
    fun testWriteLocksRead() {
        runBlocking {
            launch {
                mutex.withReadLock {
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
        runBlocking {
            launch {
                expect(3)
                mutex.write.withLock {
                    expect(8)
                }
            }

            launch {
                expect(4)
                mutex.withReadLock {
                    expect(5)
                    yield()
                    expect(7)
                }
            }

            expect(1)
            mutex.withReadLock {
                expect(2)
                yield()
                expect(6)
            }
        }
        finish(9)
    }

    private var actionIndex = AtomicInteger(0)
    private var finished = AtomicBoolean(false)
    private fun expect(index: Int) {
        val wasIndex = actionIndex.incrementAndGet()
        check(index == wasIndex) { "Expecting action index $index but it is actually $wasIndex" }
    }

    private fun finish(index: Int) {
        expect(index)
        check(!finished.getAndSet(true)) { "Should call 'finish(...)' at most once" }
    }
}
