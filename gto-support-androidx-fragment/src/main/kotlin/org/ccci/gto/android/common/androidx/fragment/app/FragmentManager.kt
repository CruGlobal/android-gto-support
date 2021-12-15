package org.ccci.gto.android.common.androidx.fragment.app

import androidx.fragment.app.FragmentManager

internal fun FragmentManager.backStackEntriesIterator() = object : ListIterator<FragmentManager.BackStackEntry> {
    private val manager = this@backStackEntriesIterator
    private var i = 0

    override fun hasNext() = i < manager.backStackEntryCount
    override fun next() = manager.getBackStackEntryAt(i++)
    override fun hasPrevious() = i > 0
    override fun previous() = manager.getBackStackEntryAt(--i)
    override fun nextIndex() = i
    override fun previousIndex() = i - 1
}

val FragmentManager.backStackEntries
    get() = object : Sequence<FragmentManager.BackStackEntry> {
        override fun iterator() = backStackEntriesIterator()
    }
