package org.ccci.gto.android.common.material.chip.databinding

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import org.ccci.gto.android.common.material.chip.elevationCompat

@BindingAdapter("elevation")
internal fun Chip.setElevationCompat(elevation: Float) {
    elevationCompat = elevation
}
