package com.squareup.picasso

import androidx.annotation.VisibleForTesting
import org.ccci.gto.android.common.util.getDeclaredFieldOrNull

@VisibleForTesting
internal val picassoField by lazy { getDeclaredFieldOrNull<RequestCreator>("picasso") }
internal val RequestCreator.picasso get() = picassoField?.get(this) as? Picasso
