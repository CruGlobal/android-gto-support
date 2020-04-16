package org.ccci.gto.android.common.recyclerview.advrecyclerview.draggable

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder

class DraggableItemViewHolderDelegate : DraggableItemViewHolder {
    private val dragState = DraggableItemState()

    override fun getDragState() = dragState
    override fun getDragStateFlags() = dragState.flags
    override fun setDragStateFlags(flags: Int) {
        dragState.flags = flags
    }
}
