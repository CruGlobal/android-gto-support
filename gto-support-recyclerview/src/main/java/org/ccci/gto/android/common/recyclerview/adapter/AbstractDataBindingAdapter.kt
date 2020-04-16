package org.ccci.gto.android.common.recyclerview.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.karumi.weak.weak

abstract class AbstractDataBindingAdapter<B : ViewDataBinding, VH : DataBindingViewHolder<B>>(
    lifecycleOwner: LifecycleOwner? = null
) : RecyclerView.Adapter<VH>() {
    private val lifecycleOwner by weak(lifecycleOwner)

    // region Lifecycle
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { onCreateViewHolder(it, viewType) }

    final override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, holder.binding, position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun onCreateViewDataBinding(parent: ViewGroup, viewType: Int): B
    protected abstract fun onCreateViewHolder(binding: B, viewType: Int): VH
    protected abstract fun onBindViewHolder(holder: VH, binding: B, position: Int)

    final override fun onViewRecycled(holder: VH) = onViewRecycled(holder, holder.binding)
    protected open fun onViewRecycled(holder: VH, binding: B) = Unit
    // end Lifecycle
}
