package org.ccci.gto.android.common.util.view

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes

fun ViewGroup.calculateTopOffsetOrNull(@IdRes descendant: Int) =
    findViewById<View>(descendant)?.let { calculateTopOffset(it) }
fun ViewGroup.calculateTopOffset(descendant: View) = with(Rect()) {
    descendant.getDrawingRect(this)
    offsetDescendantRectToMyCoords(descendant, this)
    top
}
