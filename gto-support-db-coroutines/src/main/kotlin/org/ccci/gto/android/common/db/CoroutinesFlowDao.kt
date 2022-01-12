package org.ccci.gto.android.common.db

import androidx.annotation.AnyThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface CoroutinesFlowDao : CoroutinesDao, Dao {
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun invalidationFlow(types: Collection<Class<*>>) = when {
        types.isEmpty() -> flowOf(Unit)
        else -> callbackFlow {
            val callback = Dao.InvalidationCallback { if (it in types) trySendBlocking(it) }
            registerInvalidationCallback(callback)
            send(Unit)
            awaitClose { unregisterInvalidationCallback(callback) }
        }.conflate()
    }

    @AnyThread
    fun <T : Any> findAsFlow(clazz: Class<T>, vararg key: Any) =
        invalidationFlow(listOf(clazz)).map { find(clazz, *key) }.flowOn(Dispatchers.IO)

    @AnyThread
    fun <T : Any> getAsFlow(query: Query<T>) =
        invalidationFlow(query.allTables.map { it.type }.toSet()).map { get(query) }.flowOn(Dispatchers.IO)

    @AnyThread
    fun <T : Any> getCursorAsFlow(query: Query<T>) =
        invalidationFlow(query.allTables.map { it.type }.toSet()).map { getCursor(query) }.flowOn(Dispatchers.IO)
}

inline fun <reified T : Any> CoroutinesFlowDao.findAsFlow(vararg key: Any) = findAsFlow(T::class.java, *key)
fun <T : Any> Query<T>.getAsFlow(dao: CoroutinesFlowDao) = dao.getAsFlow(this)
fun <T : Any> Query<T>.getCursorAsFlow(dao: CoroutinesFlowDao) = dao.getCursorAsFlow(this)
