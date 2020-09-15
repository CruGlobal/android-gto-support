package org.ccci.gto.android.common.recyclerview.advrecyclerview.draggable

import androidx.databinding.ViewDataBinding
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder
import org.ccci.gto.android.common.recyclerview.adapter.DataBindingViewHolder

open class DataBindingDraggableItemViewHolder<B : ViewDataBinding>(binding: B) :
    DataBindingViewHolder<B>(binding), DraggableItemViewHolder by DraggableItemViewHolderDelegate()
