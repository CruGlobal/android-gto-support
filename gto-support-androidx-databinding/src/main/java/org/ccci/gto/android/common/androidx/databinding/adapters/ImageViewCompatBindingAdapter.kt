package org.ccci.gto.android.common.androidx.databinding.adapters

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("tint")
internal fun ImageView.bindImageTintListCompat(tint: ColorStateList?) = ImageViewCompat.setImageTintList(this, tint)
