package org.ccci.gto.android.common.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import org.ccci.gto.android.common.androidx.recyclerview.adapter.SimpleDataBindingAdapter

@Deprecated("Since v3.11.2, use SimpleDataBindingAdapter from gto-support-androidx-recyclerview instead.")
abstract class SimpleDataBindingAdapter<B : ViewDataBinding>(lifecycleOwner: LifecycleOwner? = null) :
    SimpleDataBindingAdapter<B>(lifecycleOwner)
