package org.ccci.gto.android.common.picasso.widget

import android.graphics.drawable.Drawable
import android.widget.TextView
import org.ccci.gto.android.common.picasso.BaseViewTarget
import org.ccci.gto.android.common.picasso.R

class TextViewDrawableEndTarget(view: TextView) : BaseViewTarget<TextView>(view) {
    init {
        view.setTag(R.id.picasso_textViewDrawableEndTarget, this)
    }

    companion object {
        fun of(view: TextView) = view.getTag(R.id.picasso_textViewDrawableEndTarget) as? TextViewDrawableEndTarget
            ?: TextViewDrawableEndTarget(view)
    }

    public override fun updateDrawable(drawable: Drawable?) {
        val current = view.compoundDrawablesRelative
        drawable?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        view.setCompoundDrawablesRelative(current[0], current[1], drawable, current[3])
    }
}
