package org.ccci.gto.android.common.db

import android.annotation.SuppressLint
import android.database.Cursor
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.LiveData
import org.ccci.gto.android.common.androidx.lifecycle.notNull

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
interface LiveDataDao : Dao {
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    val liveDataRegistry get() = getService { LiveDataRegistry(this) }

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
        .notNull()

    @MainThread
    @SuppressLint("RestrictedApi")
    fun <T : Any> getCursorLiveData(query: Query<T>): LiveData<Cursor> = DaoGetCursorComputableLiveData(this, query)
        .also { with(liveDataRegistry) { it.registerFor(query) } }
        .liveData
        .notNull()
}

// region DaoComputableLiveData
@SuppressLint("RestrictedApi")
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
internal sealed class DaoComputableLiveData<T>(protected val dao: LiveDataDao) : ComputableLiveData<T>()

private class DaoFindComputableLiveData<T : Any>(
    dao: LiveDataDao,
    private val clazz: Class<T>,
    private vararg val key: Any,
) : DaoComputableLiveData<T?>(dao) {
    @SuppressLint("RestrictedApi")
    override fun compute() = dao.find(clazz, *key)
}

private class DaoGetComputableLiveData<T : Any>(dao: LiveDataDao, private val query: Query<T>) :
    DaoComputableLiveData<List<T>>(dao) {
    @SuppressLint("RestrictedApi")
    override fun compute() = dao.get(query)
}

private class DaoGetCursorComputableLiveData(dao: LiveDataDao, private val query: Query<*>) :
    DaoComputableLiveData<Cursor>(dao) {
    @SuppressLint("RestrictedApi")
    override fun compute() = dao.getCursor(query)
}
// endregion DaoComputableLiveData

@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
inline fun <reified T : Any> LiveDataDao.findLiveData(vararg key: Any) = findLiveData(T::class.java, *key)
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
fun <T : Any> Query<T>.getAsLiveData(dao: LiveDataDao) = dao.getLiveData(this)
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
fun <T : Any> Query<T>.getCursorAsLiveData(dao: LiveDataDao) = dao.getCursorLiveData(this)
