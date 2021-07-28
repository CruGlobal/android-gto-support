package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import androidx.concurrent.futures.CallbackToFutureAdapter
import com.google.common.util.concurrent.ListenableFuture

interface AsyncDao : Dao {
    fun <T : Any> getAsync(query: Query<T>) = runAsync { get(query) }

    fun getCursorAsync(query: Query<*>) = runAsync { getCursor(query) }

    fun <T : Any> findAsync(clazz: Class<T>, vararg key: Any) = runAsync { find(clazz, *key) }

    fun insertAsync(obj: Any) = insertAsync(obj, SQLiteDatabase.CONFLICT_NONE)
    fun insertAsync(obj: Any, conflictAlgorithm: Int) = runAsync { insert(obj, conflictAlgorithm) }

    fun updateAsync(obj: Any) = updateAsync(obj, *getFullProjection(obj.javaClass))
    fun updateAsync(obj: Any, vararg projection: String) = runAsync { update(obj, *projection) }

    fun <T : Any> updateAsync(sample: T, where: Expression?, vararg projection: String) =
        runAsync { update(sample, where, *projection) }

    fun updateOrInsertAsync(obj: Any) = updateOrInsertAsync(obj, *getFullProjection(obj.javaClass))
    fun updateOrInsertAsync(obj: Any, vararg projection: String) = runAsync { updateOrInsert(obj, *projection) }

    companion object {
        @JvmSynthetic
        inline fun <T> AsyncDao.runAsync(crossinline block: () -> T): ListenableFuture<T> =
            CallbackToFutureAdapter.getFuture<T> {
                backgroundExecutor.execute {
                    try {
                        it.set(block())
                    } catch (t: Throwable) {
                        it.setException(t)
                    }
                }
            }
    }
}

@JvmSynthetic
inline fun <reified T : Any> AsyncDao.findAsync(vararg key: Any) = findAsync(T::class.java, *key)
