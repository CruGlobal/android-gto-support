package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

sealed class CollectionLiveData<T, C : Collection<T>, MC : MutableCollection<T>>(
    private val collection: MC
) : LiveData<C>(collection as C) {
    operator fun plusAssign(item: T) {
        collection.add(item)
        value = value
    }

    operator fun plusAssign(items: Collection<T>) {
        collection.addAll(items)
        value = value
    }

    operator fun minusAssign(item: T) {
        collection.remove(item)
        value = value
    }

    operator fun minusAssign(items: Collection<T>) {
        collection.removeAll(items)
        value = value
    }

    fun clear() {
        collection.clear()
        value = value
    }
}

class ListLiveData<T> : CollectionLiveData<T, List<T>, MutableList<T>>(mutableListOf())
class SetLiveData<T> : CollectionLiveData<T, Set<T>, MutableSet<T>>(mutableSetOf())
