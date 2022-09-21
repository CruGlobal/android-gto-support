package org.ccci.gto.android.common.picasso.material

import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import com.google.android.material.chip.Chip
import org.ccci.gto.android.common.picasso.BaseViewTarget
import org.ccci.gto.android.common.picasso.R

class ChipIconTarget private constructor(chip: Chip) : BaseViewTarget<Chip>(chip) {
    init {
        view.setTag(R.id.picasso_chipIconTarget, this)
    }

    companion object {
        fun of(chip: Chip) = chip.getTag(R.id.picasso_chipIconTarget) as? ChipIconTarget ?: ChipIconTarget(chip)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun updateDrawable(drawable: Drawable?) {
        view.chipIcon = drawable
    }
}
