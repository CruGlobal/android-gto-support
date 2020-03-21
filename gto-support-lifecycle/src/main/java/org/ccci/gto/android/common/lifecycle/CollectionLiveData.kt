package org.ccci.gto.android.common.lifecycle

import org.ccci.gto.android.common.androidx.lifecycle.CollectionLiveDataShim

@Deprecated("Since v3.4.0, use version in gto-support-androidx-lifecycle instead")
sealed class CollectionLiveData<T, C : Collection<T>>(collection: ChangeAwareCollection<T>) :
    CollectionLiveDataShim<T, C>(collection)

@Deprecated("Since v3.4.0, use version in gto-support-androidx-lifecycle instead")
class ListLiveData<T> : CollectionLiveData<T, List<T>>(ChangeAwareList()) {
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

@Deprecated("Since v3.4.0, use version in gto-support-androidx-lifecycle instead")
class SetLiveData<T> : CollectionLiveData<T, Set<T>>(ChangeAwareSet()) {
    private class ChangeAwareSet<T>(
        private val delegate: MutableSet<T> = mutableSetOf()
    ) : ChangeAwareCollection<T>(delegate), MutableSet<T>, Set<T> by delegate {
        // TODO: we should handle any changes made via the iterators
        override fun iterator() = delegate.iterator()
    }
}
