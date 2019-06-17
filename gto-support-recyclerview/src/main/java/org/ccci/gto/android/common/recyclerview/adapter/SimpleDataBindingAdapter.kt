package org.ccci.gto.android.common.recyclerview.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.karumi.weak.weak

abstract class SimpleDataBindingAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<B>>() {
    private val lifecycleOwner by weak(lifecycleOwner)

    // region Lifecycle

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { DataBindingViewHolder(it) }

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
