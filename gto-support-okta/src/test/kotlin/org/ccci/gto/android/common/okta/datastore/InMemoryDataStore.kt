package org.ccci.gto.android.common.okta.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {
    private val _data =
        MutableSharedFlow<T>(replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            .also { it.tryEmit(initialValue) }

    override val data: Flow<T> = _data

    private val updateLock = Mutex()
    override suspend fun updateData(transform: suspend (t: T) -> T): T = updateLock.withLock {
        transform(_data.first()).also { _data.emit(it) }
    }
}
