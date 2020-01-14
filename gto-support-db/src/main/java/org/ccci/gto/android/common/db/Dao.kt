package org.ccci.gto.android.common.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import java.util.concurrent.Executor

interface Dao {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP, RestrictTo.Scope.SUBCLASSES)
    val backgroundExecutor: Executor

    // region Queries
    // region Read-Only
    @WorkerThread
    fun <T> find(clazz: Class<T>, vararg key: Any): T?

    @WorkerThread
    fun <T> get(query: Query<T>): List<T>

    @WorkerThread
    fun getCursor(query: Query<*>): Cursor
    // endregion Read-Only

    // region Read-Write
    @JvmDefault
    @WorkerThread
    fun <T : Any> insert(obj: T) = insert(obj, SQLiteDatabase.CONFLICT_NONE)

    @WorkerThread
    fun <T : Any> insert(obj: T, conflictAlgorithm: Int): Long
    // endregion Read-Write
    // endregion Queries
}

inline fun Query<*>.getCursor(dao: Dao) = dao.getCursor(this)
