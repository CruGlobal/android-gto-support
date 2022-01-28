package org.ccci.gto.android.common.androidx.constraintlayout.databinding

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import org.ccci.gto.android.common.androidx.constraintlayout.widget.setDimensionRatio

@BindingAdapter("layout_constraintDimensionRatio")
internal fun View.setConstraintLayoutDimensionRatio(ratio: String?) {
    val lp = layoutParams as? ConstraintLayout.LayoutParams ?: return
    if (ratio != lp.dimensionRatio) {
        lp.setDimensionRatio(ratio)
        lp.validate()
        layoutParams = lp
    }
}
