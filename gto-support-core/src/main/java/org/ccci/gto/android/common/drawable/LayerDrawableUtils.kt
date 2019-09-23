@file:JvmName("LayerDrawableUtils")
package org.ccci.gto.android.common.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable

fun LayerDrawable.children(): Sequence<Drawable?> = (0 until numberOfLayers).asSequence().map { getDrawable(it) }
