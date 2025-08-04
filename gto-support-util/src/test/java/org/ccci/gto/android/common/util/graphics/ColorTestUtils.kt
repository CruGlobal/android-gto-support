package org.ccci.gto.android.common.util.graphics

import kotlin.test.assertEquals

val HSL_CYAN = HslColor(180f, 1f, 0.5f)
val HSL_LTGRAY = HslColor(0f, 0f, 0.8f)
val HSL_WHITE = HslColor(0f, 0f, 1f)

val HSV_CYAN = HsvColor(180f, 1f, 1f)
val HSV_LTGRAY = HsvColor(0f, 0f, 0.8f)
val HSV_WHITE = HsvColor(0f, 0f, 1f)

fun assertHsvEquals(expected: HsvColor, actual: HsvColor) {
    assertEquals(expected.hue, actual.hue, 0.000001f)
    assertEquals(expected.saturation, actual.saturation, 0.000001f)
    assertEquals(expected.value, actual.value, 0.000001f)
}
