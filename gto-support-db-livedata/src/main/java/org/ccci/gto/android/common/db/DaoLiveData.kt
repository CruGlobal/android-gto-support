package org.ccci.gto.android.common.db

import android.database.Cursor
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

sealed class DaoLiveData<T>(protected val dao: Dao) : LiveData<T>() {
    @WorkerThread
    protected abstract fun loadData(): T

    @AnyThread
    fun reloadData() = dao.executeInBackground {
        // TODO: move away from blocking synchronization
        synchronized(this) { postValue(loadData()) }
    }
}

internal class DaoFindLiveData<T>(dao: Dao, private val clazz: Class<T>, private vararg val key: Any) :
    DaoLiveData<T?>(dao) {
    override fun loadData() = dao.find(clazz, *key)
}

internal class DaoGetLiveData<T>(dao: Dao, private val query: Query<T>) : DaoLiveData<List<T>>(dao) {
    override fun loadData() = dao.get(query)
}

internal class DaoGetCursorLiveData<T>(dao: Dao, private val query: Query<T>) : DaoLiveData<Cursor>(dao) {
    override fun loadData() = dao.getCursor(query)
}
