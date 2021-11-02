package org.ccci.gto.android.common.androidx.databinding.adapters

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

@BindingAdapter("android:layout_marginTop")
internal fun View.setLayoutMarginTop(t: Int) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return

    if (lp.topMargin != t) {
        lp.topMargin = t
        layoutParams = lp
    }
}
