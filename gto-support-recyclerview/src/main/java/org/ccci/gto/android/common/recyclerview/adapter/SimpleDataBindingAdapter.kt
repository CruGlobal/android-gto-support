package org.ccci.gto.android.common.recyclerview.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleDataBindingAdapter<B : ViewDataBinding> : RecyclerView.Adapter<DataBindingViewHolder<B>>() {
    // region Lifecycle

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataBindingViewHolder(onCreateViewDataBinding(parent, viewType))

    override fun onBindViewHolder(holder: DataBindingViewHolder<B>, position: Int) {
        onBindViewDataBinding(holder.binding, position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun onCreateViewDataBinding(parent: ViewGroup, viewType: Int): B
    protected abstract fun onBindViewDataBinding(binding: B, position: Int)
    override fun onViewRecycled(holder: DataBindingViewHolder<B>) = onViewDataBindingRecycled(holder.binding)
    protected open fun onViewDataBindingRecycled(binding: B) = Unit

    // end Lifecycle
}
