package org.ccci.gto.android.common.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import org.ccci.gto.android.common.androidx.recyclerview.adapter.AbstractDataBindingAdapter
import org.ccci.gto.android.common.androidx.recyclerview.adapter.DataBindingViewHolder

@Deprecated("Since v3.11.2, use AbstractDataBindingAdapter from gto-support-androidx-recyclerview instead.")
abstract class AbstractDataBindingAdapter<B : ViewDataBinding, VH : DataBindingViewHolder<B>>(
    lifecycleOwner: LifecycleOwner? = null
) : AbstractDataBindingAdapter<B, VH>(lifecycleOwner)
