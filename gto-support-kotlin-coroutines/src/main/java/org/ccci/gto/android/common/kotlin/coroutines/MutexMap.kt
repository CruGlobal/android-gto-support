package org.ccci.gto.android.common.kotlin.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutexMap {
    private val mutexes = mutableMapOf<Any, Mutex>()
    private val mapMutex = Mutex()
    suspend fun getMutex(key: Any) = mapMutex.withLock { mutexes.getOrPut(key) { Mutex() } }
}

suspend inline fun <T> MutexMap.withLock(key: Any, action: () -> T) = getMutex(key).withLock(action = action)
