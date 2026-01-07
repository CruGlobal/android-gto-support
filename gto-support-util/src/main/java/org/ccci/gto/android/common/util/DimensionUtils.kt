@file:JvmName("DimensionUtils")
@file:Suppress("NOTHING_TO_INLINE")

package org.ccci.gto.android.common.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

@JvmInline
value class Px(val value: Float) {
    constructor(value: Number) : this(value.toFloat())

    /**
     * Convert this value to an Int pixel size.
     *
     * This follows the same semantics as [android.util.TypedValue.complexToDimensionPixelSize] of requiring any
     * non-zero dimension to be at least 1 pixel.
     */
    fun toPixelSize() = when (val resp = (if (value >= 0) value + 0.5f else value - 0.5f).toInt()) {
        0 -> when {
            value == 0f -> 0
            value > 0f -> 1
            else -> -1
        }

        else -> resp
    }
}
@JvmInline
value class Dp(val value: Float) {
    constructor(value: Number) : this(value.toFloat())
}
@JvmInline
value class Sp(val value: Float)

inline fun Dp.toPx(context: Context) = toPx(context.resources)
inline fun Dp.toPx(resources: Resources) = toPx(resources.displayMetrics)
inline fun Dp.toPx(metrics: DisplayMetrics) = Px(value * metrics.density)

inline fun Sp.toPx(context: Context) = toPx(context.resources)
inline fun Sp.toPx(resources: Resources) = toPx(resources.displayMetrics)
inline fun Sp.toPx(metrics: DisplayMetrics) = Px(value * metrics.scaledDensity)

@JvmName("dpToPixelSize")
inline fun Dp.toPixelSize(context: Context) = toPixelSize(context.resources)
@JvmName("dpToPixelSize")
inline fun Dp.toPixelSize(resources: Resources) = toPixelSize(resources.displayMetrics)
@JvmName("dpToPixelSize")
inline fun Dp.toPixelSize(metrics: DisplayMetrics) = toPx(metrics).toPixelSize()

inline fun dpToPixelSize(dp: Int, context: Context) = Dp(dp).toPixelSize(context)
inline fun dpToPixelSize(dp: Int, resources: Resources) = Dp(dp).toPixelSize(resources)
inline fun dpToPixelSize(dp: Int, metrics: DisplayMetrics) = Dp(dp).toPixelSize(metrics)
