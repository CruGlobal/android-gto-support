package org.ccci.gto.android.common.androidx.compose.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.luminance

/**
 * A low level of alpha used to represent disabled components, such as text in a disabled Button.
 *
 * Copied from [androidx.compose.material3.DisabledAlpha]
 */
@Suppress("ktlint:standard:property-naming")
const val DisabledAlpha = 0.38f

val ColorScheme.isLight get() = background.luminance() > 0.5f
