package org.ccci.gto.android.common.material.chip

import android.os.Build
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

@get:JvmName("getElevation")
@set:JvmName("setElevation")
var Chip.elevationCompat
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> elevation
        else -> (chipDrawable as? ChipDrawable)?.elevation ?: 0f
    }
    set(value) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> elevation = value
            else -> (chipDrawable as? ChipDrawable)?.elevation = value
        }
    }
