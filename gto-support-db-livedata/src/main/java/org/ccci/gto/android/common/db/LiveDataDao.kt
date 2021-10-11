package org.ccci.gto.android.common.db

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.collection.SimpleArrayMap
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.LiveData
import java.util.WeakHashMap

interface LiveDataDao : Dao {
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    val liveDataRegistry: LiveDataRegistry

    @MainThread
    @SuppressLint("RestrictedApi")
    fun <T : Any> findLiveData(clazz: Class<T>, vararg key: Any) = DaoFindComputableLiveData(this, clazz, *key)
        .also { with(liveDataRegistry) { it.registerFor(clazz) } }
        .liveData

    @MainThread
    @SuppressLint("RestrictedApi")
    fun <T : Any> getLiveData(query: Query<T>): LiveData<List<T>> = DaoGetComputableLiveData(this, query)
        .also { with(liveDataRegistry) { it.registerFor(query) } }
        .liveData

    @MainThread
    @SuppressLint("RestrictedApi")
    fun <T : Any> getCursorLiveData(query: Query<T>): LiveData<Cursor> = DaoGetCursorComputableLiveData(this, query)
        .also { with(liveDataRegistry) { it.registerFor(query) } }
        .liveData
}

// region DaoComputableLiveData
@SuppressLint("RestrictedApi")
internal sealed class DaoComputableLiveData<T>(protected val dao: LiveDataDao) :
    ComputableLiveData<T>(dao.backgroundExecutor)

private class DaoFindComputableLiveData<T : Any>(
    dao: LiveDataDao,
    private val clazz: Class<T>,
    private vararg val key: Any
) : DaoComputableLiveData<T?>(dao) {
    override fun compute() = dao.find(clazz, *key)
}

private class DaoGetComputableLiveData<T : Any>(dao: LiveDataDao, private val query: Query<T>) :
    DaoComputableLiveData<List<T>>(dao) {
    override fun compute() = dao.get(query)
}

private class DaoGetCursorComputableLiveData<T : Any>(dao: LiveDataDao, private val query: Query<T>) :
    DaoComputableLiveData<Cursor>(dao) {
    override fun compute() = dao.getCursor(query)
}
// endregion DaoComputableLiveData

class LiveDataRegistry {
    private val registry: SimpleArrayMap<Class<*>, MutableMap<ComputableLiveData<*>, Unit>> = SimpleArrayMap()

    @MainThread
    internal fun DaoComputableLiveData<*>.registerFor(clazz: Class<*>) {
        synchronized(registry) {
            (registry[clazz] ?: WeakHashMap<ComputableLiveData<*>, Unit>().also { registry.put(clazz, it) })[this] =
                Unit
        }
    }

    @MainThread
    internal fun DaoComputableLiveData<*>.registerFor(query: Query<*>) {
        registerFor(query.table)
        query.joins.forEach { registerFor(it) }
    }

    @MainThread
    private fun DaoComputableLiveData<*>.registerFor(table: Table<*>) = registerFor(table.type)

    @MainThread
    private fun DaoComputableLiveData<*>.registerFor(join: Join<*, *>) {
        join.base?.let { registerFor(it) }
        registerFor(join.target)
    }

    @AnyThread
    @SuppressLint("RestrictedApi")
    fun invalidate(clazz: Class<*>) {
        synchronized(registry) {
            registry[clazz]?.keys?.forEach { it.invalidate() }
        }
    }
}

inline fun <reified T : Any> LiveDataDao.findLiveData(vararg key: Any) = findLiveData(T::class.java, *key)
fun <T : Any> Query<T>.getAsLiveData(dao: LiveDataDao) = dao.getLiveData(this)
fun <T : Any> Query<T>.getCursorAsLiveData(dao: LiveDataDao) = dao.getCursorLiveData(this)
