package org.ccci.gto.android.common.util.graphics

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Size
import kotlin.math.min

@JvmInline
value class HsvColor(@Size(3) internal val hsv: FloatArray = FloatArray(3)) {
    constructor(hue: Float, saturation: Float, value: Float) : this(arrayOf(hue, saturation, value).toFloatArray())

    val hue get() = hsv[0]
    val saturation get() = hsv[1]
    val value get() = hsv[2]

    @ColorInt
    fun toColorInt() = Color.HSVToColor(hsv)

    fun toHslColor(): HslColor {
        // sourced from: https://en.wikipedia.org/wiki/HSL_and_HSV#HSV_to_HSL
        val lightness = (1f - (saturation / 2f)) * value
        return HslColor(
            hue = hue,
            saturation = when (lightness) {
                0f, 1f -> 0f
                else -> (value - lightness) / min(lightness, 1 - lightness)
            },
            lightness = lightness
        )
    }
}

fun @receiver:ColorInt Int.toHsvColor() = HsvColor().also { Color.colorToHSV(this, it.hsv) }
