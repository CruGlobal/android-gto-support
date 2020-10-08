package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
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
        runBlocking {
            launch {
                expect(3)
                mutex.write.withLock {
                    expect(8)
                }
            }

            launch {
                expect(4)
                mutex.read.withLock {
                    expect(5)
                    yield()
                    expect(7)
                }
            }

            expect(1)
            mutex.read.withLock {
                expect(2)
                yield()
                expect(6)
            }
        }
        finish(9)
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
