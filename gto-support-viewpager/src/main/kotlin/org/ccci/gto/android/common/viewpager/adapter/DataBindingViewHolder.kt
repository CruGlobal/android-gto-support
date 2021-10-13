package org.ccci.gto.android.common.viewpager.adapter

import androidx.databinding.ViewDataBinding

open class DataBindingViewHolder<B : ViewDataBinding>(val binding: B) : ViewHolderPagerAdapter.ViewHolder(binding.root)
