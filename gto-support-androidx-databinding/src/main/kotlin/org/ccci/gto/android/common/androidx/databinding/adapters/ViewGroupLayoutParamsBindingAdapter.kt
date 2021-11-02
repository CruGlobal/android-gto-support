package org.ccci.gto.android.common.androidx.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:layout_height")
internal fun View.setLayoutHeight(h: Int) {
    val lp = layoutParams

    if (lp.height != h) {
        lp.height = h
        layoutParams = lp
    }
}
