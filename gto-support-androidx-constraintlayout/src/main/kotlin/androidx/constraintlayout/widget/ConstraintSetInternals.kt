package androidx.constraintlayout.widget

internal object ConstraintSetInternals {
    fun parseDimensionRatioString(params: ConstraintLayout.LayoutParams, value: String?) =
        ConstraintSet.parseDimensionRatioString(params, value)
}
