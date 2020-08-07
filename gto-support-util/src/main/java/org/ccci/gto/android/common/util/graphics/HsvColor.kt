package org.ccci.gto.android.common.util.graphics

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Size

inline class HsvColor(@Size(3) internal val hsv: FloatArray = FloatArray(3)) {
    constructor(hue: Float, saturation: Float, value: Float) : this(arrayOf(hue, saturation, value).toFloatArray())

    val hue get() = hsv[0]
    val saturation get() = hsv[1]
    val value get() = hsv[2]

    @ColorInt
    fun toColorInt() = Color.HSVToColor(hsv)
}

fun @receiver:ColorInt Int.toHsvColor() = HsvColor().also { Color.colorToHSV(this, it.hsv) }
