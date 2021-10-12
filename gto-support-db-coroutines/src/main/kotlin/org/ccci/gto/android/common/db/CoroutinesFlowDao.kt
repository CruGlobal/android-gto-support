package org.ccci.gto.android.common.db

import androidx.annotation.AnyThread
import androidx.lifecycle.asFlow

interface CoroutinesFlowDao : CoroutinesDao, LiveDataDao {
    @AnyThread
    fun <T : Any> findAsFlow(clazz: Class<T>, vararg key: Any) = findLiveData(clazz, *key).asFlow()

    @AnyThread
    fun <T : Any> getAsFlow(query: Query<T>) = getLiveData(query).asFlow()

    @AnyThread
    fun <T : Any> getCursorAsFlow(query: Query<T>) = getCursorLiveData(query).asFlow()
}

inline fun <reified T : Any> CoroutinesFlowDao.findAsFlow(vararg key: Any) = findAsFlow(T::class.java, *key)
fun <T : Any> Query<T>.getAsFlow(dao: CoroutinesFlowDao) = dao.getAsFlow(this)
fun <T : Any> Query<T>.getCursorAsFlow(dao: CoroutinesFlowDao) = dao.getCursorAsFlow(this)
