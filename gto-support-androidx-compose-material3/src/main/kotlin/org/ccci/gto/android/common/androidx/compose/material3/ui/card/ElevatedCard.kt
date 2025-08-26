package org.ccci.gto.android.common.androidx.compose.material3.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: Dp = CardDefaults.elevatedCardDefaultElevation,
    contentVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) = Surface(
    modifier = modifier,
    shape = shape,
    color = colors.containerColor,
    contentColor = colors.contentColor,
    shadowElevation = elevation,
) {
    Column(
        verticalArrangement = contentVerticalArrangement,
        content = content,
    )
}
