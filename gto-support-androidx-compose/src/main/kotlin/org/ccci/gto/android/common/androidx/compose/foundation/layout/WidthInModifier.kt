package org.ccci.gto.android.common.androidx.compose.foundation.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrain

fun Modifier.widthIn(min: (Dp) -> Dp = { it }, max: (Dp) -> Dp = { it }) = then(object : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val transformedConstraints = constraints.constrain(
            Constraints(
                minWidth = min(constraints.minWidth.toDp()).roundToPx(),
                maxWidth = max(constraints.maxWidth.toDp()).roundToPx()
            )
        )
        val placeable = measurable.measure(transformedConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
})
