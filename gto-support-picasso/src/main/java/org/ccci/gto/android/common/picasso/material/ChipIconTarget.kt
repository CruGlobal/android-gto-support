package org.ccci.gto.android.common.picasso.material

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.ccci.gto.android.common.picasso.R

class ChipIconTarget private constructor(private val chip: Chip) : Target {
    init {
        chip.setTag(R.id.picasso_chipIconTarget, this)
    }

    companion object {
        fun of(chip: Chip) = chip.getTag(R.id.picasso_chipIconTarget) as? ChipIconTarget ?: ChipIconTarget(chip)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        chip.chipIcon = placeHolderDrawable
    }

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        chip.chipIcon = BitmapDrawable(chip.resources, bitmap)
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        chip.chipIcon = errorDrawable
    }
}
