package org.ccci.gto.android.common.androidx.compose.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.luminance

val ColorScheme.isLight get() = background.luminance() > 0.5f
