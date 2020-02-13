package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

sealed class CollectionLiveData<T, C : Collection<T>, MC : MutableCollection<T>>(
    private val collection: MC
) : LiveData<C>(collection as C) {
    fun add(item: T) {
        collection.add(item)
        value = value
    }

    fun addAll(items: Collection<T>) {
        collection.addAll(items)
        value = value
    }

    fun remove(item: T) {
        collection.remove(item)
        value = value
    }

    fun removeAll(items: Collection<T>) {
        collection.removeAll(items)
        value = value
    }

    fun clear() {
        collection.clear()
        value = value
    }

    operator fun plusAssign(item: T) = add(item)
    operator fun plusAssign(items: Collection<T>) = addAll(items)
    operator fun minusAssign(item: T) = remove(item)
    operator fun minusAssign(items: Collection<T>) = removeAll(items)
}

class ListLiveData<T> : CollectionLiveData<T, List<T>, MutableList<T>>(mutableListOf())
class SetLiveData<T> : CollectionLiveData<T, Set<T>, MutableSet<T>>(mutableSetOf())
