package org.ccci.gto.android.common.util.graphics

import androidx.annotation.ColorInt
import kotlin.math.min

data class HslColor(val hue: Float, val saturation: Float, val lightness: Float) {
    fun darken(percentage: Float) = copy(lightness = (lightness - percentage).coerceAtLeast(0f))

    fun toHsvColor(): HsvColor {
        // sourced from: https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_HSV
        val value = lightness + saturation * min(lightness, 1 - lightness)
        return HsvColor(
            hue = hue,
            saturation = when (value) {
                0f -> 0f
                else -> 2 * (1 - (lightness / value))
            },
            value = value
        )
    }

    @ColorInt
    fun toColorInt() = toHsvColor().toColorInt()
}

fun @receiver:ColorInt Int.toHslColor() = toHsvColor().toHslColor()
