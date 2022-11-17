package org.ccci.gto.android.common.androidx.compose.material3

import androidx.compose.ui.graphics.Color

fun Color.disabledAlphaIf(state: Boolean) = if (state) copy(alpha = DisabledAlpha) else this
