package org.ccci.gto.android.common.picasso.widget

import android.graphics.drawable.Drawable
import android.widget.TextView
import org.ccci.gto.android.common.picasso.BaseViewTarget
import org.ccci.gto.android.common.picasso.R

class TextViewDrawableStartTarget(view: TextView) : BaseViewTarget<TextView>(view) {
    init {
        view.setTag(R.id.picasso_textViewDrawableStartTarget, this)
    }

    companion object {
        fun of(view: TextView) = view.getTag(R.id.picasso_textViewDrawableStartTarget) as? TextViewDrawableStartTarget
            ?: TextViewDrawableStartTarget(view)
    }

    public override fun updateDrawable(drawable: Drawable?) {
        val current = view.compoundDrawablesRelative
        drawable?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
        view.setCompoundDrawablesRelative(drawable, current[1], current[2], current[3])
    }
}
