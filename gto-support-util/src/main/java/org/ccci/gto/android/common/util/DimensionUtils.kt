@file:JvmName("DimensionUtils")
@file:Suppress("NOTHING_TO_INLINE")

package org.ccci.gto.android.common.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.roundToInt

inline class Px(val value: Float)
inline class Dp(val value: Float)
inline class Sp(val value: Float)

inline fun Dp(value: Int) = Dp(value.toFloat())

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
inline fun Dp.toPixelSize(metrics: DisplayMetrics) = toPx(metrics).value.roundToInt()

inline fun dpToPixelSize(dp: Int, context: Context) = Dp(dp).toPixelSize(context)
inline fun dpToPixelSize(dp: Int, resources: Resources) = Dp(dp).toPixelSize(resources)
inline fun dpToPixelSize(dp: Int, metrics: DisplayMetrics) = Dp(dp).toPixelSize(metrics)
