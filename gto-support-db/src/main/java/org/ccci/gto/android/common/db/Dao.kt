package org.ccci.gto.android.common.db

import android.database.Cursor
import android.os.AsyncTask
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import java.util.concurrent.Executor

interface Dao {
    @WorkerThread
    fun <T> find(clazz: Class<T>, vararg key: Any): T?

    @WorkerThread
    fun <T> get(query: Query<T>): List<T>

    @WorkerThread
    fun getCursor(query: Query<*>): Cursor

    @JvmDefault
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP, RestrictTo.Scope.SUBCLASSES)
    val backgroundExecutor: Executor get() = AsyncTask.THREAD_POOL_EXECUTOR
}
