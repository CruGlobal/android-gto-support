package org.ccci.gto.android.common.material.chip

import com.google.android.material.chip.Chip

@Deprecated("Since v3.9.0, use Chip.elevation directly")
@get:JvmName("getElevation")
@set:JvmName("setElevation")
var Chip.elevationCompat
    get() = elevation
    set(value) {
        elevation = value
    }
