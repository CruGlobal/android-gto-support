package org.ccci.gto.android.common.material.shape

import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.ShapePath
import kotlin.math.sin

class AngledCutCornerTreatment(private val startEdgeSize: Float, private val endEdgeSize: Float) : CornerTreatment() {
    override fun getCornerPath(shapePath: ShapePath, angle: Float, interpolation: Float, radius: Float) {
        shapePath.reset(0f, startEdgeSize * interpolation, 180f, 180 - angle)
        shapePath.lineTo(
            (sin(Math.toRadians(angle.toDouble())) * endEdgeSize * interpolation).toFloat(),
            // Something about using cos() is causing rounding which prevents the path from being convex
            // on api levels 21 and 22. Using sin() with 90 - angle is helping for now.
            (sin(Math.toRadians(90 - angle.toDouble())) * startEdgeSize * interpolation).toFloat()
        )
    }
}
