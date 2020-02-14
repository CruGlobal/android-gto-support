package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

sealed class CollectionLiveData<T, C : Collection<T>, MC : MutableCollection<T>>(
    private val collection: MC
) : LiveData<C>(collection as C) {
    fun add(item: T) = collection.add(item).also { if (it) notifyChanged() }
    fun addAll(items: Collection<T>) = collection.addAll(items).also { if (it) notifyChanged() }
    fun remove(item: T) = collection.remove(item).also { if (it) notifyChanged() }
    fun removeAll(items: Collection<T>) = collection.removeAll(items).also { if (it) notifyChanged() }

    fun clear() {
        collection.clear()
        notifyChanged()
    }

    private fun notifyChanged() = postValue(collection as C)

    @JvmSynthetic
    operator fun plusAssign(item: T) {
        add(item)
    }

    @JvmSynthetic
    operator fun plusAssign(items: Collection<T>) {
        addAll(items)
    }

    @JvmSynthetic
    operator fun minusAssign(item: T) {
        remove(item)
    }

    @JvmSynthetic
    operator fun minusAssign(items: Collection<T>) {
        removeAll(items)
    }
}

class ListLiveData<T> : CollectionLiveData<T, List<T>, MutableList<T>>(mutableListOf())
class SetLiveData<T> : CollectionLiveData<T, Set<T>, MutableSet<T>>(mutableSetOf())
