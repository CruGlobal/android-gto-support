package org.ccci.gto.android.common.androidx.viewpager2.adapter

import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

abstract class PrimaryItemChangeObserver<VH : RecyclerView.ViewHolder>(
    recyclerView: RecyclerView,
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

    private var primaryItemId: Long? = null

    fun register() {
        viewPager.registerOnPageChangeCallback(pageChangeObserver)
        adapter.registerAdapterDataObserver(dataObserver)
    }

    fun unregister() {
        adapter.unregisterAdapterDataObserver(dataObserver)
        viewPager.unregisterOnPageChangeCallback(pageChangeObserver)
    }

    abstract fun onUpdatePrimaryItemId(primaryItemId: Long?, previousPrimaryItemId: Long?)

    private fun updatePrimaryItem() {
        // only update when the ViewPager is idle
        if (viewPager.scrollState != ViewPager2.SCROLL_STATE_IDLE) return

        // clear the previous primary item if we had one and the adapter is now empty
        val previous = primaryItemId
        if (adapter.itemCount == 0 && primaryItemId != null) {
            primaryItemId = null
            onUpdatePrimaryItemId(null, previous)
        }

        // current item is yet to be updated; it is guaranteed to change, so we will be notified via
        // [ViewPager2.OnPageChangeCallback.onPageSelected]
        val currentItem = viewPager.currentItem
        if (currentItem >= adapter.itemCount) return

        // find the ViewHolder for the current item
        val current = adapter.getItemId(currentItem)
        if (current != previous) {
            primaryItemId = current
            onUpdatePrimaryItemId(current, previous)
        }
    }
}

private fun RecyclerView.inferViewPager() =
    parent as? ViewPager2 ?: throw IllegalStateException("Expected ViewPager2 instance. Got: $parent")

fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.onUpdatePrimaryItem(
    recyclerView: RecyclerView,
    block: (primaryItemId: Long?, previousPrimaryItemId: Long?) -> Unit
) = object : PrimaryItemChangeObserver<VH>(recyclerView, this) {
    override fun onUpdatePrimaryItemId(primaryItemId: Long?, previousPrimaryItemId: Long?) =
        block(primaryItemId, previousPrimaryItemId)
}.also { it.register() }
