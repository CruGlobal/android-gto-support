package org.ccci.gto.android.common.viewpager.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.karumi.weak.weak

abstract class DataBindingPagerAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    BaseDataBindingPagerAdapter<B, DataBindingViewHolder<B>>(lifecycleOwner) {
    final override fun onCreateViewHolder(parent: ViewGroup) = DataBindingViewHolder(
        onCreateViewDataBinding(parent)
            .also { it.lifecycleOwner = lifecycleOwner }
    )

    abstract fun onCreateViewDataBinding(parent: ViewGroup): B
}

abstract class BaseDataBindingPagerAdapter<B : ViewDataBinding, VH : DataBindingViewHolder<B>>(
    lifecycleOwner: LifecycleOwner? = null
) : ViewHolderPagerAdapter<VH>() {
    protected val lifecycleOwner by weak(lifecycleOwner)

    final override fun onBindViewHolder(holder: VH, position: Int) {
        super.onBindViewHolder(holder, position)
        onBindViewDataBinding(holder, holder.binding, position)
    }

    final override fun onViewHolderRecycled(holder: VH) {
        super.onViewHolderRecycled(holder)
        onViewDataBindingRecycled(holder, holder.binding)
    }

    protected open fun onBindViewDataBinding(holder: VH, binding: B, position: Int) = Unit
    protected open fun onViewDataBindingRecycled(holder: VH, binding: B) = Unit
}
