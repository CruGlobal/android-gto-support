package org.ccci.gto.android.common.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import org.ccci.gto.android.common.androidx.recyclerview.adapter.DataBindingViewHolder

abstract class SimpleDataBindingAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    AbstractDataBindingAdapter<B, DataBindingViewHolder<B>>(lifecycleOwner) {
    final override fun onCreateViewHolder(binding: B, viewType: Int) = DataBindingViewHolder(binding)
    final override fun onBindViewHolder(holder: DataBindingViewHolder<B>, binding: B, position: Int) =
        onBindViewDataBinding(binding, position)
    final override fun onViewRecycled(holder: DataBindingViewHolder<B>, binding: B) = onViewDataBindingRecycled(binding)

    protected abstract fun onBindViewDataBinding(binding: B, position: Int)
    protected open fun onViewDataBindingRecycled(binding: B) = Unit
}
