package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ReadWriteMutex {
    private val stateMutex = Mutex()
    private val writeMutex = Mutex()
    private var readers = 0

    suspend fun <T> withWriteLock(owner: Any? = null, action: suspend () -> T): T =
        writeMutex.withLock(owner) { action() }

    suspend fun <T> withReadLock(action: suspend () -> T): T {
        stateMutex.withLock {
            // first reader should lock the write mutex
            if (readers == 0) writeMutex.lock()
            readers++
        }

        try {
            return action()
        } finally {
            stateMutex.withLock {
                readers--
                // release the write mutex lock when this is the last reader
                if (readers == 0) writeMutex.unlock()
            }
        }
    }
}
