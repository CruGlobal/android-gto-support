package org.ccci.gto.android.common.compose.ui.draw

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent

fun Modifier.invisibleIf(invisible: Boolean) = if (invisible) drawWithContent { } else this

inline fun Modifier.invisibleIf(crossinline invisible: () -> Boolean) =
    drawWithContent { if (!invisible()) drawContent() }
