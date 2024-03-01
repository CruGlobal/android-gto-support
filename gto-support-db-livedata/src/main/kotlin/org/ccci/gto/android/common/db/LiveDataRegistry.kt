package org.ccci.gto.android.common.db

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.collection.SimpleArrayMap
import androidx.lifecycle.ComputableLiveData
import java.util.WeakHashMap

class LiveDataRegistry(dao: Dao) {
    @SuppressLint("RestrictedApi")
    private val registry: SimpleArrayMap<Class<*>, MutableMap<ComputableLiveData<*>, Unit>> = SimpleArrayMap()

    init {
        dao.registerInvalidationCallback(this::invalidate)
    }

    @MainThread
    internal fun DaoComputableLiveData<*>.registerFor(clazz: Class<*>) {
        synchronized(registry) {
            (registry[clazz] ?: WeakHashMap<ComputableLiveData<*>, Unit>().also { registry.put(clazz, it) })[this] =
                Unit
        }
    }

    @MainThread
    internal fun DaoComputableLiveData<*>.registerFor(query: Query<*>) {
        query.allTables.forEach { registerFor(it.type) }
    }

    @WorkerThread
    @SuppressLint("RestrictedApi")
    internal fun invalidate(clazz: Class<*>) {
        synchronized(registry) {
            registry[clazz]?.keys?.forEach { it.invalidate() }
        }
    }
}
