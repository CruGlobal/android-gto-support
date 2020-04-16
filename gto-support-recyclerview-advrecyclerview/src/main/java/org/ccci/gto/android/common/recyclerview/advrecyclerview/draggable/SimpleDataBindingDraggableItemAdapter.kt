package org.ccci.gto.android.common.recyclerview.advrecyclerview.draggable

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import org.ccci.gto.android.common.recyclerview.adapter.AbstractDataBindingAdapter

abstract class SimpleDataBindingDraggableItemAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    AbstractDataBindingAdapter<B, DataBindingDraggableItemViewHolder<B>>(lifecycleOwner),
    DraggableItemAdapter<DataBindingDraggableItemViewHolder<B>> {
    final override fun onCreateViewHolder(binding: B, viewType: Int) = DataBindingDraggableItemViewHolder(binding)
    final override fun onBindViewHolder(holder: DataBindingDraggableItemViewHolder<B>, binding: B, position: Int) =
        onBindViewDataBinding(binding, position)
    final override fun onViewRecycled(holder: DataBindingDraggableItemViewHolder<B>, binding: B) =
        onViewDataBindingRecycled(binding)

    protected abstract fun onBindViewDataBinding(binding: B, position: Int)
    protected open fun onViewDataBindingRecycled(binding: B) = Unit
}
