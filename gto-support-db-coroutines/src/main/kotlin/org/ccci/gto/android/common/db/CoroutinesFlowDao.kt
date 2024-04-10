package org.ccci.gto.android.common.db

import androidx.annotation.AnyThread
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
interface CoroutinesFlowDao : CoroutinesDao, Dao {
    private fun invalidationFlow(types: Collection<Class<*>>, emitOnStart: Boolean = true) = when {
        types.isEmpty() -> if (emitOnStart) flowOf(Unit) else emptyFlow()
        else -> callbackFlow {
            val callback = Dao.InvalidationCallback { if (it in types) trySendBlocking(Unit) }
            registerInvalidationCallback(callback)
            if (emitOnStart) send(Unit)
            awaitClose { unregisterInvalidationCallback(callback) }
        }.conflate()
    }

    @AnyThread
    fun invalidationFlow(vararg types: Class<*>, emitOnStart: Boolean = true) =
        invalidationFlow(types.toSet(), emitOnStart)

    @AnyThread
    fun <T : Any> findAsFlow(clazz: Class<T>, vararg key: Any) =
        invalidationFlow(listOf(clazz)).map { find(clazz, *key) }.flowOn(coroutineDispatcher)

    @AnyThread
    fun <T : Any> getAsFlow(query: Query<T>) =
        invalidationFlow(query.allTables.map { it.type }.toSet()).map { get(query) }.flowOn(coroutineDispatcher)

    @AnyThread
    fun <T : Any> getCursorAsFlow(query: Query<T>) =
        invalidationFlow(query.allTables.map { it.type }.toSet()).map { getCursor(query) }.flowOn(coroutineDispatcher)
}

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
inline fun <reified T : Any> CoroutinesFlowDao.findAsFlow(vararg key: Any) = findAsFlow(T::class.java, *key)
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
fun <T : Any> Query<T>.getAsFlow(dao: CoroutinesFlowDao) = dao.getAsFlow(this)
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
fun <T : Any> Query<T>.getCursorAsFlow(dao: CoroutinesFlowDao) = dao.getCursorAsFlow(this)
