package org.ccci.gto.android.common.androidx.compose.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.resolveDefaults
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val EmptyTextReplacement = "HHHHHHHHHH"

@Composable
fun computeHeightForDefaultText(
    textStyle: TextStyle,
    lines: Int = 1
): Int {
    require(lines >= 0) { "Invalid number of lines: $lines" }

    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    val resolvedStyle = remember(textStyle, layoutDirection) {
        resolveDefaults(textStyle, layoutDirection)
    }
    val typeface by remember(fontFamilyResolver, resolvedStyle) {
        fontFamilyResolver.resolve(
            resolvedStyle.fontFamily,
            resolvedStyle.fontWeight ?: FontWeight.Normal,
            resolvedStyle.fontStyle ?: FontStyle.Normal,
            resolvedStyle.fontSynthesis ?: FontSynthesis.All
        )
    }

    return remember(density, fontFamilyResolver, textStyle, layoutDirection, typeface) {
        computeHeightForDefaultText(resolvedStyle, density, fontFamilyResolver, lines)
    }
}

fun computeHeightForDefaultText(
    style: TextStyle,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    lines: Int = 1
): Int {
    require(lines >= 0) { "Invalid number of lines: $lines" }

    return computeSizeForDefaultText(
        style, density, fontFamilyResolver,
        text = Array(lines) { EmptyTextReplacement }.joinToString("\n"),
        maxLines = lines
    ).height
}

private fun computeSizeForDefaultText(
    style: TextStyle,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    text: String = EmptyTextReplacement,
    maxLines: Int = 1
): IntSize {
    val paragraph = Paragraph(
        text = text,
        style = style,
        spanStyles = listOf(),
        maxLines = maxLines,
        ellipsis = false,
        density = density,
        fontFamilyResolver = fontFamilyResolver,
        constraints = Constraints()
    )
    return IntSize(paragraph.minIntrinsicWidth.toIntPx(), paragraph.height.toIntPx())
}

private fun Float.toIntPx(): Int = ceil(this).roundToInt()
