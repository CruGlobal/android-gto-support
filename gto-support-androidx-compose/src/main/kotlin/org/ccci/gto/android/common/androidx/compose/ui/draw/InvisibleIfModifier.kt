package org.ccci.gto.android.common.androidx.compose.ui.draw

import androidx.compose.ui.Modifier
import org.ccci.gto.android.common.compose.ui.draw.invisibleIf

@Deprecated(
    "Since v4.4.0, use the multiplatform invisibleIf instead",
    replaceWith = ReplaceWith(
        "invisibleIf(invisible)",
        imports = ["org.ccci.gto.android.common.compose.ui.draw.invisibleIf"],
    ),
)
fun Modifier.invisibleIf(invisible: Boolean) = invisibleIf(invisible)

@Deprecated(
    "Since v4.4.0, use the multiplatform invisibleIf instead",
    replaceWith = ReplaceWith(
        "invisibleIf(invisible)",
        imports = ["org.ccci.gto.android.common.compose.ui.draw.invisibleIf"],
    ),
)
inline fun Modifier.invisibleIf(crossinline invisible: () -> Boolean) = invisibleIf(invisible)
