package org.ccci.gto.android.common.androidx.compose.material3.ui.pullrefresh

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshIndicator(
    refreshing: Boolean,
    state: PullRefreshState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(backgroundColor),
    scale: Boolean = false,
) = androidx.compose.material.pullrefresh.PullRefreshIndicator(
    refreshing = refreshing,
    state = state,
    modifier = modifier,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    scale = scale,
)
