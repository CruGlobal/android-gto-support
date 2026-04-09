package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutexMap {
    private val mutexes = mutableMapOf<Any, Mutex>()
    private val mapMutex = Mutex()
    suspend operator fun get(key: Any) = mapMutex.withLock { mutexes.getOrPut(key) { Mutex() } }

    @Deprecated("Since v4.5.1, Use get() instead", ReplaceWith("get(key)"))
    suspend fun getMutex(key: Any) = get(key)
}

suspend inline fun <T> MutexMap.withLock(key: Any, action: () -> T) = this[key].withLock(action = action)
