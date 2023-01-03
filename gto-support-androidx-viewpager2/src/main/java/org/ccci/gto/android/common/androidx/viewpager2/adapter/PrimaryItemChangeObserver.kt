package org.ccci.gto.android.common.androidx.viewpager2.adapter

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

abstract class PrimaryItemChangeObserver<VH : RecyclerView.ViewHolder>(
    private val recyclerView: RecyclerView,
    private val adapter: RecyclerView.Adapter<VH>,
) {
    private val viewPager = recyclerView.inferViewPager()
    @VisibleForTesting
    internal val pageChangeObserver = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) = updatePrimaryItem()
        override fun onPageScrollStateChanged(state: Int) = updatePrimaryItem()
    }
    @VisibleForTesting
    internal val childAttachStateChangeListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) = updatePrimaryItem()
        override fun onChildViewDetachedFromWindow(view: View) = updatePrimaryItem()
    }

    init {
        require(adapter.hasStableIds()) { "The adapter needs stable ids to monitor changes to the primary item" }
    }

    private var primaryItemId: Long? = null
    private var primaryItem: VH? = null

    fun register() {
        viewPager.registerOnPageChangeCallback(pageChangeObserver)
        recyclerView.addOnChildAttachStateChangeListener(childAttachStateChangeListener)
    }

    fun unregister() {
        recyclerView.removeOnChildAttachStateChangeListener(childAttachStateChangeListener)
        viewPager.unregisterOnPageChangeCallback(pageChangeObserver)
    }

    /**
     * This method is called whenever the primary item ViewHolder is changed.
     *
     * @param primaryItem The current primary item ViewHolder, this should not be tracked locally because it could be
     * recycled at any point after this method returns and no longer be valid.
     * @param previousItem The previous primary item ViewHolder, this should not be tracked locally because it could be
     * recycled at any point after this method returns and no longer be valid.
     */
    abstract fun onUpdatePrimaryItem(primaryItem: VH?, previousItem: VH?)

    private fun updatePrimaryItem() {
        // only update when the ViewPager is idle
        if (viewPager.scrollState != ViewPager2.SCROLL_STATE_IDLE) return

        // determine the current primary item
        val primaryId = viewPager.currentItem.takeIf { it < adapter.itemCount }?.let { adapter.getItemId(it) }
        val primary = primaryId?.let { recyclerView.findViewHolderForItemId(it) as VH? }

        // short-circuit if the primary item hasn't changed or we are in an intermediate state
        if (primaryId == primaryItemId && primary === primaryItem) return
        if (primaryId != null && primary == null) return

        // trigger the callback with the updated primary item
        val previous = primaryItem
        primaryItemId = primaryId
        primaryItem = primary
        onUpdatePrimaryItem(primaryItem, previous)
    }
}

private fun RecyclerView.inferViewPager() =
    parent as? ViewPager2 ?: throw IllegalStateException("Expected ViewPager2 instance. Got: $parent")

fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.onUpdatePrimaryItem(
    recyclerView: RecyclerView,
    block: (primaryItem: VH?, previousItem: VH?) -> Unit,
) = object : PrimaryItemChangeObserver<VH>(recyclerView, this) {
    override fun onUpdatePrimaryItem(primaryItem: VH?, previousItem: VH?) = block(primaryItem, previousItem)
}.also { it.register() }
