package org.ccci.gto.android.common.material.shape

import com.google.android.material.shape.ShapePath
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import kotlin.random.Random
import org.junit.Test

class AngledCutCornerTreatmentTest {
    @Test
    fun verifyGetCornerPathFor90DegreeCorner() {
        val start = Random.nextFloat()
        val end = Random.nextFloat()
        val treatment = AngledCutCornerTreatment(start, end)

        val shapePath: ShapePath = mock()
        treatment.getCornerPath(shapePath, 90f, 1f, 0f)
        inOrder(shapePath) {
            verify(shapePath).reset(0f, start, 180f, 90f)
            verify(shapePath).lineTo(end, 0f)
        }
    }
}
