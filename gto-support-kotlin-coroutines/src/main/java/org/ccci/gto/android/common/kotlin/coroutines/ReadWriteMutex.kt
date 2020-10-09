package org.ccci.gto.android.common.kotlin.coroutines

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ReadWriteMutex {
    val write: Mutex
    val read: Mutex
}

fun ReadWriteMutex(): ReadWriteMutex = ReadWriteMutexImpl()

@VisibleForTesting
internal class ReadWriteMutexImpl : ReadWriteMutex {
    private val stateMutex = Mutex()
    private val readerOwner = Any()
    @VisibleForTesting
    internal var readers = 0L

    override val write = Mutex()
    override val read = object : Mutex {
        override suspend fun lock(owner: Any?) {
            stateMutex.withLock {
                check(readers < Long.MAX_VALUE) {
                    "Attempt to lock the read mutex more than ${Long.MAX_VALUE} times concurrently"
                }
                // first reader should lock the write mutex
                if (readers == 0L) write.lock(readerOwner)
                readers++
            }
        }

        override fun unlock(owner: Any?) {
            runBlocking {
                stateMutex.withLock {
                    check(readers > 0L) { "Attempt to unlock the read mutex when it wasn't locked" }
                    // release the write mutex lock when this is the last reader
                    if (--readers == 0L) write.unlock(readerOwner)
                }
            }
        }

        override val isLocked get() = TODO("Not supported")
        override val onLock get() = TODO("Not supported")
        override fun holdsLock(owner: Any) = TODO("Not supported")
        override fun tryLock(owner: Any?) = TODO("Not supported")
    }
}
