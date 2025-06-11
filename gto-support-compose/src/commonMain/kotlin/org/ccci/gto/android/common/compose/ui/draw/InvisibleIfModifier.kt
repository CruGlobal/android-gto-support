package org.ccci.gto.android.common.compose.ui.draw

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.semantics.clearAndSetSemantics

fun Modifier.invisibleIf(invisible: Boolean) = when {
    invisible -> drawWithContent { }.clearAndSetSemantics { }
    else -> this
}

@Deprecated("Since v4.4.2, There is no performance benefit of deferring the read of invisible()")
inline fun Modifier.invisibleIf(crossinline invisible: () -> Boolean) = invisibleIf(invisible())
