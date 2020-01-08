package org.ccci.gto.android.common.db

import android.database.Cursor
import androidx.annotation.WorkerThread

interface Dao {
    @WorkerThread
    fun <T> find(clazz: Class<T>, vararg key: Any): T?

    @WorkerThread
    fun <T> get(query: Query<T>): List<T>

    @WorkerThread
    fun getCursor(query: Query<*>): Cursor
}
