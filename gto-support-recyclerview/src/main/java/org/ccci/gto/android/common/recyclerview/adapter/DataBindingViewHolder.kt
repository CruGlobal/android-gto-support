package org.ccci.gto.android.common.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class DataBindingViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)
