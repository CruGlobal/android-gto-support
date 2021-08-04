package org.ccci.gto.android.common.picasso.material.button

import android.graphics.drawable.Drawable
import com.google.android.material.button.MaterialButton
import org.ccci.gto.android.common.picasso.BaseViewTarget
import org.ccci.gto.android.common.picasso.R

class MaterialButtonIconTarget private constructor(button: MaterialButton) : BaseViewTarget<MaterialButton>(button) {
    init {
        view.setTag(R.id.picasso_materialButtonIconTarget, this)
    }

    companion object {
        fun of(button: MaterialButton) =
            button.getTag(R.id.picasso_materialButtonIconTarget) as? MaterialButtonIconTarget
                ?: MaterialButtonIconTarget(button)
    }

    public override fun updateDrawable(drawable: Drawable?) {
        view.icon = drawable
    }
}
