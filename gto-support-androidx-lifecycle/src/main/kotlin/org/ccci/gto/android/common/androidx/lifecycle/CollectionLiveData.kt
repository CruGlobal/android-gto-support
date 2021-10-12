package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData

sealed class CollectionLiveData<T, C : Collection<T>>(
    private val collection: ChangeAwareCollection<T>,
    private val synchronous: Boolean
) : LiveData<C>(collection as C) {
    init {
        collection.liveData = this
    }

    fun add(item: T) = collection.add(item)
    fun addAll(items: Collection<T>) = collection.addAll(items)
    fun remove(item: T) = collection.remove(item)
    fun removeAll(items: Collection<T>) = collection.removeAll(items)
    fun removeAll(predicate: (T) -> Boolean) = collection.removeAll(predicate).also { if (it) notifyChanged() }
    fun retainAll(elements: Collection<T>) = collection.retainAll(elements)
    fun clear() = collection.clear()

    private fun notifyChanged() = (collection as C).let { if (synchronous) setValue(it) else postValue(it) }

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

    protected abstract class ChangeAwareCollection<T>(
        private val delegate: MutableCollection<T>
    ) : MutableCollection<T> {
        internal lateinit var liveData: CollectionLiveData<*, *>
        protected fun notifyChanged() {
            liveData.notifyChanged()
        }

        override fun add(element: T) = delegate.add(element).also { if (it) notifyChanged() }
        override fun addAll(elements: Collection<T>) = delegate.addAll(elements).also { if (it) notifyChanged() }
        override fun remove(element: T) = delegate.remove(element).also { if (it) notifyChanged() }
        override fun removeAll(elements: Collection<T>) = delegate.removeAll(elements).also { if (it) notifyChanged() }
        override fun retainAll(elements: Collection<T>) = delegate.retainAll(elements).also { if (it) notifyChanged() }
        override fun clear() = delegate.clear().also { notifyChanged() }
    }
}

class ListLiveData<T>(synchronous: Boolean = false) : CollectionLiveData<T, List<T>>(ChangeAwareList(), synchronous) {
    private class ChangeAwareList<T>(
        private val delegate: MutableList<T> = mutableListOf()
    ) : ChangeAwareCollection<T>(delegate), MutableList<T>, List<T> by delegate {
        override fun add(index: Int, element: T) = delegate.add(index, element).also { notifyChanged() }
        override fun addAll(index: Int, elements: Collection<T>) =
            delegate.addAll(index, elements).also { if (it) notifyChanged() }
        override fun removeAt(index: Int) = delegate.removeAt(index).also { notifyChanged() }
        override fun set(index: Int, element: T) = delegate.set(index, element).also { notifyChanged() }

        // TODO: we should handle any changes made via the iterators
        override fun iterator() = delegate.iterator()
        override fun listIterator() = delegate.listIterator()
        override fun listIterator(index: Int) = delegate.listIterator(index)
        override fun subList(fromIndex: Int, toIndex: Int) = delegate.subList(fromIndex, toIndex)
    }
}

class SetLiveData<T>(synchronous: Boolean = false) : CollectionLiveData<T, Set<T>>(ChangeAwareSet(), synchronous) {
    private class ChangeAwareSet<T>(
        private val delegate: MutableSet<T> = mutableSetOf()
    ) : ChangeAwareCollection<T>(delegate), MutableSet<T>, Set<T> by delegate {
        // TODO: we should handle any changes made via the iterators
        override fun iterator() = delegate.iterator()
    }
}
