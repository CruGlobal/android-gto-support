package org.ccci.gto.android.common.compat.graphics.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable

@Deprecated("Since v4.5.0, use InsetDrawable instead")
class InsetDrawableCompat(drawable: Drawable?, insetLeft: Int, insetTop: Int, insetRight: Int, insetBottom: Int) :
    InsetDrawable(drawable, insetLeft, insetTop, insetRight, insetBottom) {
    constructor(drawable: Drawable?, inset: Int) : this(drawable, inset, inset, inset, inset)
}
