package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.async

interface CoroutinesAsyncDao : CoroutinesDao {
    fun <T : Any> findAsync(clazz: Class<T>, vararg key: Any) = coroutineScope.async { find(clazz, *key) }
    fun <T : Any> getAsync(query: Query<T>) = coroutineScope.async { get(query) }
    fun getCursorAsync(query: Query<*>) = coroutineScope.async { getCursor(query) }

    fun insertAsync(obj: Any, conflictAlgorithm: Int = SQLiteDatabase.CONFLICT_NONE) =
        coroutineScope.async { insert(obj, conflictAlgorithm) }

    fun updateAsync(obj: Any) = updateAsync(obj, *getFullProjection(obj.javaClass))
    fun updateAsync(obj: Any, vararg projection: String) = coroutineScope.async { update(obj, *projection) }
    fun <T : Any> updateAsync(sample: T, where: Expression?, vararg projection: String) =
        coroutineScope.async { update(sample, where, *projection) }

    fun updateOrInsertAsync(obj: Any) = updateOrInsertAsync(obj, *getFullProjection(obj.javaClass))
    fun updateOrInsertAsync(obj: Any, vararg projection: String) =
        coroutineScope.async { updateOrInsert(obj, *projection) }

    fun deleteAsync(obj: Any) = coroutineScope.async { delete(obj) }

    fun <T> transactionAsync(exclusive: Boolean = true, body: () -> T) =
        coroutineScope.async { transaction(exclusive, body) }
}

inline fun <reified T : Any> CoroutinesAsyncDao.findAsync(vararg key: Any) = findAsync(T::class.java, *key)
