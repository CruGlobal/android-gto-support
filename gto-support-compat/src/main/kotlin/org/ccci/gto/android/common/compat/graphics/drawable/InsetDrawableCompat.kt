package org.ccci.gto.android.common.compat.graphics.drawable

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Build

class InsetDrawableCompat(drawable: Drawable?, insetLeft: Int, insetTop: Int, insetRight: Int, insetBottom: Int) :
    InsetDrawable(drawable, insetLeft, insetTop, insetRight, insetBottom) {
    constructor(drawable: Drawable?, inset: Int) : this(drawable, inset, inset, inset, inset)

    private val tmpInsets = Rect()
    private val insets = Rect()
    private fun calculateInsets() {
        if (drawable?.getPadding(tmpInsets) == null) tmpInsets.setEmpty()
        getPadding(insets)
        insets.left -= tmpInsets.left
        insets.top -= tmpInsets.top
        insets.right -= tmpInsets.right
        insets.bottom -= tmpInsets.bottom
    }

    override fun getIntrinsicWidth() = when {
        // XXX: getIntrinsicWidth() doesn't account for horizontal insets until Lollipop MR1
        Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1 -> {
            calculateInsets()
            drawable?.let { it.intrinsicWidth + insets.left + insets.right } ?: -1
        }
        else -> super.getIntrinsicWidth()
    }

    override fun getIntrinsicHeight() = when {
        // XXX: getIntrinsicHeight() doesn't account for horizontal insets until Lollipop MR1
        Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1 -> {
            calculateInsets()
            drawable?.let { it.intrinsicHeight + insets.top + insets.bottom } ?: -1
        }
        else -> super.getIntrinsicHeight()
    }
}
