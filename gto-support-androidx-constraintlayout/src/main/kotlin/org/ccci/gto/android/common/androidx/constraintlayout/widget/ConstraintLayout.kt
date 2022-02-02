package org.ccci.gto.android.common.androidx.constraintlayout.widget

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSetInternals

fun ConstraintLayout.LayoutParams.setDimensionRatio(ratio: String?) =
    ConstraintSetInternals.parseDimensionRatioString(this, ratio)
