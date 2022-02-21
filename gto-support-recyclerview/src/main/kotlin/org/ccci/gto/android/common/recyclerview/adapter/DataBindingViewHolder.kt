package org.ccci.gto.android.common.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import org.ccci.gto.android.common.androidx.recyclerview.adapter.DataBindingViewHolder

@Deprecated("Since v3.11.2, use DataBindingViewHolder from gto-support-androidx-recyclerview instead.")
open class DataBindingViewHolder<B : ViewDataBinding>(binding: B) : DataBindingViewHolder<B>(binding)
