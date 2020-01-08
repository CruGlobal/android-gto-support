package org.ccci.gto.android.common.db

import android.database.Cursor

interface LiveDataDao : Dao {
    @JvmDefault
    fun <T> findLiveData(clazz: Class<T>, vararg key: Any): DaoLiveData<T?> = DaoFindLiveData(this, clazz, *key)

    @JvmDefault
    fun <T> getLiveData(query: Query<T>): DaoLiveData<List<T>> = DaoGetLiveData(this, query)

    @JvmDefault
    fun <T> getCursorLiveData(query: Query<T>): DaoLiveData<Cursor> = DaoGetCursorLiveData(this, query)
}

fun <T> Query<T>.asLiveData(dao: LiveDataDao) = dao.getLiveData(this)
fun <T> Query<T>.getCursorAsLiveData(dao: LiveDataDao) = dao.getCursorLiveData(this)
