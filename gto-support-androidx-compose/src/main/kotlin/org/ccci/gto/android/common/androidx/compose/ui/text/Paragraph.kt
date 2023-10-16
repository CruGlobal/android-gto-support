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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private const val EMPTY_TEXT_REPLACEMENT = "HHHHHHHHHH"

/**
 * Return the height of a block of text in Dp
 */
@Composable
fun computeHeightForDefaultText(textStyle: TextStyle, lines: Int = 1): Dp {
    require(lines >= 0) { "Invalid number of lines: $lines" }
    if (lines == 0) return 0.dp

    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    val resolvedStyle = resolveStyle(textStyle, layoutDirection)
    val typeface by rememberTypeface(resolvedStyle, fontFamilyResolver)

    return remember(density, fontFamilyResolver, textStyle, layoutDirection, typeface) {
        computeHeightForDefaultText(resolvedStyle, density, fontFamilyResolver, lines)
    }
}

private fun computeHeightForDefaultText(
    style: TextStyle,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    lines: Int = 1,
): Dp {
    require(lines >= 1) { "Invalid number of lines: $lines" }

    val paragraph = Paragraph(
        text = Array(lines) { EMPTY_TEXT_REPLACEMENT }.joinToString("\n"),
        style = style,
        maxLines = lines,
        ellipsis = false,
        density = density,
        fontFamilyResolver = fontFamilyResolver,
        constraints = Constraints()
    )
    return with(density) { paragraph.height.toDp() }
}

@Composable
fun computeWidthForSingleLineOfText(text: String, style: TextStyle): Dp {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    val resolvedStyle = resolveStyle(style, layoutDirection)
    val typeface by rememberTypeface(resolvedStyle, fontFamilyResolver)

    return remember(density, fontFamilyResolver, style, layoutDirection, typeface) {
        computeWidthForSingleLineOfText(text, resolvedStyle, fontFamilyResolver, density)
    }
}

private fun computeWidthForSingleLineOfText(
    text: String,
    style: TextStyle,
    fontFamilyResolver: FontFamily.Resolver,
    density: Density,
) = with(density) {
    Paragraph(
        text = text,
        style = style,
        maxLines = 1,
        ellipsis = false,
        density = density,
        fontFamilyResolver = fontFamilyResolver,
        constraints = Constraints()
    ).maxIntrinsicWidth.toDp()
}

@Composable
private fun resolveStyle(style: TextStyle, layoutDirection: LayoutDirection) =
    remember(style, layoutDirection) { resolveDefaults(style, layoutDirection) }

@Composable
private fun rememberTypeface(style: TextStyle, fontFamilyResolver: FontFamily.Resolver) =
    remember(style, fontFamilyResolver) {
        fontFamilyResolver.resolve(
            style.fontFamily,
            style.fontWeight ?: FontWeight.Normal,
            style.fontStyle ?: FontStyle.Normal,
            style.fontSynthesis ?: FontSynthesis.All,
        )
    }
