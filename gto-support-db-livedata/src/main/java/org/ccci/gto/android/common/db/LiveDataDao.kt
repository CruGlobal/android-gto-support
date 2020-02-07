package org.ccci.gto.android.common.db

import android.database.Cursor
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
    @JvmDefault
    fun <T> findLiveData(clazz: Class<T>, vararg key: Any): LiveData<T?> = DaoFindComputableLiveData(this, clazz, *key)
        .also { with(liveDataRegistry) { it.registerFor(clazz) } }
        .liveData

    @MainThread
    @JvmDefault
    fun <T> getLiveData(query: Query<T>): LiveData<List<T>> = DaoGetComputableLiveData(this, query)
        .also { with(liveDataRegistry) { it.registerFor(query) } }
        .liveData

    @MainThread
    @JvmDefault
    fun <T> getCursorLiveData(query: Query<T>): LiveData<Cursor> = DaoGetCursorComputableLiveData(this, query)
        .also { with(liveDataRegistry) { it.registerFor(query) } }
        .liveData
}

// region DaoComputableLiveData
internal sealed class DaoComputableLiveData<T>(protected val dao: LiveDataDao) :
    ComputableLiveData<T>(dao.backgroundExecutor)

private class DaoFindComputableLiveData<T>(dao: LiveDataDao, private val clazz: Class<T>, private vararg val key: Any) :
    DaoComputableLiveData<T?>(dao) {
    override fun compute() = dao.find(clazz, *key)
}

private class DaoGetComputableLiveData<T>(dao: LiveDataDao, private val query: Query<T>) :
    DaoComputableLiveData<List<T>>(dao) {
    override fun compute() = dao.get(query)
}

private class DaoGetCursorComputableLiveData<T>(dao: LiveDataDao, private val query: Query<T>) :
    DaoComputableLiveData<Cursor>(dao) {
    override fun compute() = dao.getCursor(query)
}
// endregion DaoComputableLiveData

@MainThread
class LiveDataRegistry {
    private val registry: SimpleArrayMap<Class<*>, MutableMap<ComputableLiveData<*>, Unit>> = SimpleArrayMap()

    internal fun DaoComputableLiveData<*>.registerFor(clazz: Class<*>) {
        (registry[clazz] ?: WeakHashMap<ComputableLiveData<*>, Unit>().also { registry.put(clazz, it) })[this] = Unit
    }

    internal fun DaoComputableLiveData<*>.registerFor(query: Query<*>) {
        registerFor(query.table)
        query.joins.forEach { registerFor(it) }
    }

    private fun DaoComputableLiveData<*>.registerFor(table: Table<*>) = registerFor(table.type)

    private fun DaoComputableLiveData<*>.registerFor(join: Join<*, *>) {
        join.base?.let { registerFor(it) }
        registerFor(join.target)
    }

    fun invalidate(clazz: Class<*>) {
        registry[clazz]?.keys?.forEach { it.invalidate() }
    }
}

inline fun <reified T> LiveDataDao.findLiveData(vararg key: Any) = findLiveData(T::class.java, *key)
fun <T> Query<T>.getAsLiveData(dao: LiveDataDao) = dao.getLiveData(this)
fun <T> Query<T>.getCursorAsLiveData(dao: LiveDataDao) = dao.getCursorLiveData(this)
