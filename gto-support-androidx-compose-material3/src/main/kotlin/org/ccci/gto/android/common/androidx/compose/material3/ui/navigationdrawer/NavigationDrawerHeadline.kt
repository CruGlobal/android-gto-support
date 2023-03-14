package org.ccci.gto.android.common.androidx.compose.material3.ui.navigationdrawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NavigationDrawerHeadline(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
) {
    Surface(
        modifier = modifier
            .height(NavigationDrawerTokens.ActiveIndicatorHeight)
            .fillMaxWidth(),
        color = colors.containerColor(false).value,
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleSmall,
                    LocalContentColor provides colors.textColor(false).value,
                    content = label,
                )
            }
        }
    }
}
