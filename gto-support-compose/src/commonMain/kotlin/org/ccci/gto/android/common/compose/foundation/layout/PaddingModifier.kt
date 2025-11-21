package org.ccci.gto.android.common.compose.foundation.layout

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.padding(
    all: Dp = 0.dp,
    horizontal: Dp = all,
    vertical: Dp = all,
    start: Dp = horizontal,
    top: Dp = vertical,
    end: Dp = horizontal,
    bottom: Dp = vertical,
) = padding(start = start, top = top, end = end, bottom = bottom)
