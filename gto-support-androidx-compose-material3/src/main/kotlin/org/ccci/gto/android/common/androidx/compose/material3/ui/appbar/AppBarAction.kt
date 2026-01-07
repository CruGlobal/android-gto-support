package org.ccci.gto.android.common.androidx.compose.material3.ui.appbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.ccci.gto.android.common.androidx.compose.ui.text.computeWidthForSingleLineOfText

data class AppBarAction(
    @DrawableRes
    internal val iconRes: Int? = null,
    @StringRes
    internal val titleRes: Int? = null,
    internal val title: String? = null,
) {
    init {
        require(iconRes != null || titleRes != null || title != null) {
            "An icon or title is required for AppBarActions"
        }
    }
}

@Composable
fun AppBarActionButton(action: AppBarAction, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val title = action.titleRes?.let { stringResource(it) } ?: action.title
    when {
        action.iconRes != null -> IconButton(onClick = onClick, modifier = modifier) {
            Icon(painterResource(action.iconRes), contentDescription = title)
        }

        title != null -> {
            val style = MaterialTheme.typography.labelLarge
            val titleWidth = computeWidthForSingleLineOfText(title, style).coerceAtLeast(40.dp)
            IconButton(onClick = onClick, modifier = modifier.widthIn(min = titleWidth)) {
                Text(title, style = style, maxLines = 1)
            }
        }
    }
}
