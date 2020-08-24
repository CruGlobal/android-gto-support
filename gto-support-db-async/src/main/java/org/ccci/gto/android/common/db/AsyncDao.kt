package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import androidx.concurrent.futures.CallbackToFutureAdapter
import com.google.common.util.concurrent.ListenableFuture

interface AsyncDao : Dao {
    @JvmDefault
    fun <T : Any> getAsync(query: Query<T>) = runAsync { get(query) }

    @JvmDefault
    fun getCursorAsync(query: Query<*>) = runAsync { getCursor(query) }

    @JvmDefault
    fun <T : Any> findAsync(clazz: Class<T>, vararg key: Any) = runAsync { find(clazz, *key) }

    @JvmDefault
    fun insertAsync(obj: Any) = insertAsync(obj, SQLiteDatabase.CONFLICT_NONE)

    @JvmDefault
    fun insertAsync(obj: Any, conflictAlgorithm: Int) = runAsync { insert(obj, conflictAlgorithm) }

    @JvmDefault
    fun updateAsync(obj: Any) = updateAsync(obj, *getFullProjection(obj.javaClass))

    @JvmDefault
    fun updateAsync(obj: Any, vararg projection: String) = runAsync { update(obj, *projection) }

    @JvmDefault
    fun <T : Any> updateAsync(sample: T, where: Expression?, vararg projection: String) =
        runAsync { update(sample, where, *projection) }

    @JvmDefault
    fun updateOrInsertAsync(obj: Any) = updateOrInsertAsync(obj, *getFullProjection(obj.javaClass))

    @JvmDefault
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
