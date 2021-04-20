package org.ccci.gto.android.common.androidx.viewpager2.adapter

import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

abstract class PrimaryItemChangeObserver<VH : RecyclerView.ViewHolder>(
    private val recyclerView: RecyclerView,
    private val adapter: RecyclerView.Adapter<VH>
) {
    private val viewPager = recyclerView.inferViewPager()
    @VisibleForTesting
    internal val pageChangeObserver = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) = updatePrimaryItem()
        override fun onPageScrollStateChanged(state: Int) = updatePrimaryItem()
    }
    @VisibleForTesting
    internal val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = updatePrimaryItem()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = updatePrimaryItem()
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = updatePrimaryItem()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = updatePrimaryItem()
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = updatePrimaryItem()
    }

    init {
        require(adapter.hasStableIds()) { "The adapter needs stable ids to monitor changes to the primary item" }
    }

    private var primaryItem: VH? = null

    fun register() {
        viewPager.registerOnPageChangeCallback(pageChangeObserver)
        adapter.registerAdapterDataObserver(dataObserver)
    }

    fun unregister() {
        adapter.unregisterAdapterDataObserver(dataObserver)
        viewPager.unregisterOnPageChangeCallback(pageChangeObserver)
    }

    abstract fun onUpdatePrimaryItem(primaryItem: VH?, previousPrimaryItem: VH?)

    private fun updatePrimaryItem() {
        // only update when the ViewPager is idle
        if (viewPager.scrollState != ViewPager2.SCROLL_STATE_IDLE) return

        // clear the previous primary item if we had one and the adapter is now empty
        val previous = primaryItem
        if (adapter.itemCount == 0 && previous != null) {
            primaryItem = null
            onUpdatePrimaryItem(null, previous)
        }

        // current item is yet to be updated; it is guaranteed to change, so we will be notified via
        // [ViewPager2.OnPageChangeCallback.onPageSelected]
        val currentItem = viewPager.currentItem
        if (currentItem >= adapter.itemCount) return

        // find the ViewHolder for the current item
        val current = recyclerView.findViewHolderForItemId(adapter.getItemId(currentItem)) as VH?
        if (current !== previous) {
            primaryItem = current
            onUpdatePrimaryItem(current, previous)
        }
    }
}

private fun RecyclerView.inferViewPager() =
    parent as? ViewPager2 ?: throw IllegalStateException("Expected ViewPager2 instance. Got: $parent")

fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.onUpdatePrimaryItem(
    recyclerView: RecyclerView,
    block: (primaryItem: VH?, previousPrimaryItem: VH?) -> Unit
) = object : PrimaryItemChangeObserver<VH>(recyclerView, this) {
    override fun onUpdatePrimaryItem(primaryItem: VH?, previousPrimaryItem: VH?) =
        block(primaryItem, previousPrimaryItem)
}.also { it.register() }
