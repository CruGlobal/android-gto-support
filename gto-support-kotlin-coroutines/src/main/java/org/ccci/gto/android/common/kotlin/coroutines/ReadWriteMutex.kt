package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ReadWriteMutex {
    val write: Mutex
    val read: Mutex

    suspend fun <T> withReadLock(action: suspend () -> T): T
}

fun ReadWriteMutex(): ReadWriteMutex = ReadWriteMutexImpl()

private class ReadWriteMutexImpl : ReadWriteMutex {
    private val stateMutex = Mutex()
    private var readers = 0

    override val write = Mutex()
    override val read = object : Mutex {
        override suspend fun lock(owner: Any?) {
            stateMutex.withLock(owner) {
                // first reader should lock the write mutex
                if (readers == 0) write.lock(owner)
                readers++
            }
        }

        override fun unlock(owner: Any?) {
            runBlocking {
                stateMutex.withLock {
                    readers--
                    // release the write mutex lock when this is the last reader
                    if (readers == 0) write.unlock()
                }
            }
        }

        override val isLocked get() = TODO("Not supported")
        override val onLock get() = TODO("Not supported")
        override fun holdsLock(owner: Any) = TODO("Not supported")
        override fun tryLock(owner: Any?) = TODO("Not supported")
    }

    override suspend fun <T> withReadLock(action: suspend () -> T): T = read.withLock { action() }
}
