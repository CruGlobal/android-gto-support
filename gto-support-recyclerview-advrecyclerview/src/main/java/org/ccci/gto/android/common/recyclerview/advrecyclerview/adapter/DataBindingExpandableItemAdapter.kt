package org.ccci.gto.android.common.recyclerview.advrecyclerview.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.karumi.weak.weak
import org.ccci.gto.android.common.recyclerview.adapter.DataBindingViewHolder

abstract class DataBindingExpandableItemAdapter<GB : ViewDataBinding, CB : ViewDataBinding>(
    lifecycleOwner: LifecycleOwner? = null
) : AbstractExpandableItemAdapter<DataBindingViewHolder<GB>, DataBindingViewHolder<CB>>() {
    private val lifecycleOwner: LifecycleOwner? by weak(lifecycleOwner)

    // region Lifecycle

    final override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateGroupViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { DataBindingViewHolder(it) }

    final override fun onBindGroupViewHolder(holder: DataBindingViewHolder<GB>, groupPosition: Int, viewType: Int) {
        onBindGroupViewDataBinding(holder.binding, groupPosition, viewType)
        holder.binding.executePendingBindings()
    }

    final override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateChildViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { DataBindingViewHolder(it) }

    final override fun onBindChildViewHolder(
        holder: DataBindingViewHolder<CB>,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    ) {
        onBindChildViewDataBinding(holder.binding, groupPosition, childPosition, viewType)
        holder.binding.executePendingBindings()
    }

    protected abstract fun onCreateGroupViewDataBinding(parent: ViewGroup, viewType: Int): GB
    protected abstract fun onBindGroupViewDataBinding(binding: GB, groupPosition: Int, viewType: Int)
    protected abstract fun onCreateChildViewDataBinding(parent: ViewGroup, viewType: Int): CB
    protected abstract fun onBindChildViewDataBinding(
        binding: CB,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    )

    // endregion Lifecycle
}
