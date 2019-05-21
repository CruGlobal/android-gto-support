package org.ccci.gto.android.common.recyclerview.advrecyclerview.adapter

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.expandable.annotation.ExpandableItemStateFlags

class DelegateExpandableItemViewHolder : ExpandableItemViewHolder {
    private val expandState = ExpandableItemState()

    override fun setExpandStateFlags(@ExpandableItemStateFlags flags: Int) {
        expandState.flags = flags
    }

    @ExpandableItemStateFlags
    override fun getExpandStateFlags() = expandState.flags

    override fun getExpandState() = expandState
}
