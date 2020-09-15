package org.ccci.gto.android.common.recyclerview.advrecyclerview.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.karumi.weak.weak
import org.ccci.gto.android.common.recyclerview.adapter.DataBindingViewHolder

abstract class DataBindingExpandableItemAdapter<GB : ViewDataBinding, CB : ViewDataBinding>(
    lifecycleOwner: LifecycleOwner? = null
) : AbstractExpandableItemAdapter<DataBindingExpandableViewHolder<GB>, DataBindingExpandableViewHolder<CB>>() {
    private val lifecycleOwner: LifecycleOwner? by weak(lifecycleOwner)

    // region Lifecycle

    final override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateGroupViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { DataBindingExpandableViewHolder(it) }

    final override fun onBindGroupViewHolder(
        holder: DataBindingExpandableViewHolder<GB>,
        groupPosition: Int,
        viewType: Int
    ) {
        onBindGroupViewDataBinding(holder, holder.binding, groupPosition, viewType)
        holder.binding.executePendingBindings()
    }

    final override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateChildViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
            .let { DataBindingExpandableViewHolder(it) }

    final override fun onBindChildViewHolder(
        holder: DataBindingExpandableViewHolder<CB>,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    ) {
        onBindChildViewDataBinding(holder, holder.binding, groupPosition, childPosition, viewType)
        holder.binding.executePendingBindings()
    }

    protected abstract fun onCreateGroupViewDataBinding(parent: ViewGroup, viewType: Int): GB
    protected abstract fun onBindGroupViewDataBinding(
        holder: DataBindingExpandableViewHolder<GB>,
        binding: GB,
        groupPosition: Int,
        viewType: Int
    )
    protected abstract fun onCreateChildViewDataBinding(parent: ViewGroup, viewType: Int): CB
    protected abstract fun onBindChildViewDataBinding(
        holder: DataBindingExpandableViewHolder<CB>,
        binding: CB,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    )

    // endregion Lifecycle
}

class DataBindingExpandableViewHolder<B : ViewDataBinding>(binding: B) :
    DataBindingViewHolder<B>(binding), ExpandableItemViewHolder by DelegateExpandableItemViewHolder()
