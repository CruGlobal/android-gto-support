package org.ccci.gto.android.common.androidx.databinding.adapters

import android.content.res.ColorStateList
import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("backgroundTintCompat")
internal fun View.bindBackgroundTintCompat(tintList: ColorStateList) = ViewCompat.setBackgroundTintList(this, tintList)
