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

    private var primaryItemId: Long? = null
    private var primaryItem: VH? = null

    fun register() {
        viewPager.registerOnPageChangeCallback(pageChangeObserver)
        adapter.registerAdapterDataObserver(dataObserver)
    }

    fun unregister() {
        adapter.unregisterAdapterDataObserver(dataObserver)
        viewPager.unregisterOnPageChangeCallback(pageChangeObserver)
    }

    /**
     * This method is called whenever the primary item is changed.
     *
     * @param primaryItem The current ViewHolder for the primary item, this item should not be stored locally because it
     * could be recycled at any point after this method returns and no longer be valid.
     * @param primaryItemId The id of the current primary item, or null if there are no items.
     * @param previousPrimaryItemId The id of the previous primary item, or null if there was none.
     */
    abstract fun onUpdatePrimaryItem(
        primaryItem: VH?,
        primaryItemId: Long?,
        previousPrimaryItemId: Long?
    )

    private fun updatePrimaryItem() {
        // only update when the ViewPager is idle
        if (viewPager.scrollState != ViewPager2.SCROLL_STATE_IDLE) return

        // determine the current primary item
        val primaryId = viewPager.currentItem.takeIf { it < adapter.itemCount }?.let { adapter.getItemId(it) }
        val primary = primaryId?.let { recyclerView.findViewHolderForItemId(it) as VH? }

        // short-circuit if we are in an intermediate state
        if (primaryId != null && primary == null) return

        // trigger a callback if the primary item changed
        if (primaryId != primaryItemId || primary !== primaryItem) {
            val previousId = primaryItemId
            primaryItemId = primaryId
            primaryItem = primary

            // it is not possible to reliably provide the previous VH to this callback due to the RecyclerView
            // potentially recycling it before updatePrimaryItem() is called.
            onUpdatePrimaryItem(primaryItem, primaryItemId, previousId)
        }
    }
}

private fun RecyclerView.inferViewPager() =
    parent as? ViewPager2 ?: throw IllegalStateException("Expected ViewPager2 instance. Got: $parent")

fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.onUpdatePrimaryItem(
    recyclerView: RecyclerView,
    block: (primaryItem: VH?, primaryItemId: Long?, previousPrimaryItemId: Long?) -> Unit
) = object : PrimaryItemChangeObserver<VH>(recyclerView, this) {
    override fun onUpdatePrimaryItem(primaryItem: VH?, primaryItemId: Long?, previousPrimaryItemId: Long?) =
        block(primaryItem, primaryItemId, previousPrimaryItemId)
}.also { it.register() }
