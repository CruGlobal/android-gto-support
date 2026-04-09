package org.ccci.gto.android.common.kotlin.coroutines

import androidx.annotation.VisibleForTesting
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ReadWriteMutex {
    val write: Mutex

    /**
     * Provides a shared read mutex. This Mutex doesn't support the `owner` parameter to debug/prevent deadlocks.
     * It is also not possible to upgrade a read lock to a write lock without release the read lock first.
     */
    val read: Mutex
}

fun ReadWriteMutex(): ReadWriteMutex = ReadWriteMutexImpl()

@VisibleForTesting
internal class ReadWriteMutexImpl : ReadWriteMutex {
    private val readLockMutex = Mutex()
    @VisibleForTesting
    internal val readers = AtomicLong(0)

    override val write = Mutex()
    override val read = object : Mutex {
        override suspend fun lock(owner: Any?) {
            readLockMutex.withLock {
                while (true) {
                    val count = readers.get()
                    check(count < Long.MAX_VALUE) {
                        "Attempt to lock the read mutex more than ${Long.MAX_VALUE} times concurrently"
                    }
                    if (count == 0L) write.lock()
                    if (readers.compareAndSet(count, count + 1)) break
                    if (count == 0L) write.unlock()
                }
            }
        }

        override fun unlock(owner: Any?) {
            var count: Long
            do {
                count = readers.get()
                check(count > 0) { "Attempt to unlock the read mutex when it wasn't locked" }
            } while (!readers.compareAndSet(count, count - 1))
            if (count == 1L) write.unlock()
        }

        override val isLocked get() = TODO("Not supported")
        override val onLock get() = TODO("Not supported")
        override fun holdsLock(owner: Any) = TODO("Not supported")
        override fun tryLock(owner: Any?) = TODO("Not supported")
    }
}
