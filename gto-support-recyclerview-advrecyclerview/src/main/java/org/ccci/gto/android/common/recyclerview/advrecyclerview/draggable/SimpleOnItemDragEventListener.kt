package org.ccci.gto.android.common.recyclerview.advrecyclerview.draggable

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager

open class SimpleOnItemDragEventListener : RecyclerViewDragDropManager.OnItemDragEventListener {
    override fun onItemDragStarted(position: Int) = Unit
    override fun onItemDragMoveDistanceUpdated(offsetX: Int, offsetY: Int) = Unit
    override fun onItemDragPositionChanged(fromPosition: Int, toPosition: Int) = Unit
    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) = Unit
}
