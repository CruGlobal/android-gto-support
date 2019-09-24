@file:JvmName("LayerDrawableUtils")
package org.ccci.gto.android.common.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable

val LayerDrawable.children: Sequence<Drawable?> get() = (0 until numberOfLayers).asSequence().map { getDrawable(it) }
