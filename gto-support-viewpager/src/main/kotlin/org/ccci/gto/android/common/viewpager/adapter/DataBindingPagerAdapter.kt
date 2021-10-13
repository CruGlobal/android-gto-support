package org.ccci.gto.android.common.viewpager.adapter

import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.karumi.weak.weak

abstract class DataBindingPagerAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    BaseDataBindingPagerAdapter<B, DataBindingViewHolder<B>>(lifecycleOwner) {
    final override fun onCreateViewHolder(parent: ViewGroup) = DataBindingViewHolder(
        onCreateViewDataBinding(parent).also {
            it.lifecycleOwner = lifecycleOwner
            onViewDataBindingCreated(it)
        }
    )

    abstract fun onCreateViewDataBinding(parent: ViewGroup): B
    open fun onViewDataBindingCreated(binding: B) = Unit
}

abstract class BaseDataBindingPagerAdapter<B : ViewDataBinding, VH : DataBindingViewHolder<B>>(
    lifecycleOwner: LifecycleOwner? = null
) : ViewHolderPagerAdapter<VH>() {
    protected val lifecycleOwner by weak(lifecycleOwner)
    protected val primaryItemBinding get() = primaryItem?.binding

    final override fun onBindViewHolder(holder: VH, position: Int) {
        super.onBindViewHolder(holder, position)
        onBindViewDataBinding(holder, holder.binding, position)
    }

    final override fun onUpdatePrimaryItem(old: VH?, holder: VH?) =
        onUpdatePrimaryItem(old, old?.binding, holder, holder?.binding)

    final override fun onViewHolderRecycled(holder: VH) {
        super.onViewHolderRecycled(holder)
        onViewDataBindingRecycled(holder, holder.binding)
    }

    @UiThread
    protected open fun onBindViewDataBinding(holder: VH, binding: B, position: Int) = Unit
    @UiThread
    protected open fun onUpdatePrimaryItem(oldHolder: VH?, oldBinding: B?, holder: VH?, binding: B?) = Unit
    @UiThread
    protected open fun onViewDataBindingRecycled(holder: VH, binding: B) = Unit
}
