package org.ccci.gto.android.common.material.progressindicator.databinding

import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.BaseProgressIndicator

@BindingAdapter("indicatorColor")
internal fun BaseProgressIndicator<*>.setSingleIndicatorColor(color: Int) = setIndicatorColor(color)
