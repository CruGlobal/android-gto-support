package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ReadWriteMutex {
    val write: Mutex

    suspend fun <T> withWriteLock(owner: Any? = null, action: suspend () -> T): T
    suspend fun <T> withReadLock(action: suspend () -> T): T
}

fun ReadWriteMutex(): ReadWriteMutex = ReadWriteMutexImpl()

private class ReadWriteMutexImpl : ReadWriteMutex {
    private val stateMutex = Mutex()
    override val write = Mutex()
    private var readers = 0

    override suspend fun <T> withWriteLock(owner: Any?, action: suspend () -> T): T = write.withLock(owner) { action() }

    override suspend fun <T> withReadLock(action: suspend () -> T): T {
        stateMutex.withLock {
            // first reader should lock the write mutex
            if (readers == 0) write.lock()
            readers++
        }

        try {
            return action()
        } finally {
            stateMutex.withLock {
                readers--
                // release the write mutex lock when this is the last reader
                if (readers == 0) write.unlock()
            }
        }
    }
}
