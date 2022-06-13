package org.ccci.gto.android.common.androidx.compose.foundation.text

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.resolveDefaults
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import kotlin.math.ceil
import kotlin.math.roundToInt

// Logic based on MaxLinesHeightModifier from Android Open Source Project.

private const val EmptyTextReplacement = "HHHHHHHHHH"

/**
 * Constraint the height of the text field so that it vertically occupies at least [minLines]
 * number of lines.
 *
 * This modifier can be deprecated once upstream support for minLines exists.
 * related ticket: https://issuetracker.google.com/issues/122476634
 */
fun Modifier.minLinesHeight(
    minLines: Int,
    textStyle: TextStyle
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "minLinesHeight"
        properties["minLines"] = minLines
        properties["textStyle"] = textStyle
    }
) {
    require(minLines > 0) { "minLines must be greater than 0" }
    if (minLines == Int.MAX_VALUE) return@composed Modifier

    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    // Difference between the height of two lines paragraph and one line paragraph gives us
    // an approximation of height of one line
    val resolvedStyle = remember(textStyle, layoutDirection) {
        resolveDefaults(textStyle, layoutDirection)
    }

    val firstLineHeight = remember(
        density,
        fontFamilyResolver,
        textStyle,
        layoutDirection
    ) {
        computeSizeForDefaultText(
            style = resolvedStyle,
            density = density,
            fontFamilyResolver = fontFamilyResolver,
            text = EmptyTextReplacement,
            maxLines = 1
        ).height
    }

    val firstTwoLinesHeight = remember(
        density,
        fontFamilyResolver,
        textStyle,
        layoutDirection
    ) {
        val twoLines = EmptyTextReplacement + "\n" + EmptyTextReplacement
        computeSizeForDefaultText(
            style = resolvedStyle,
            density = density,
            fontFamilyResolver = fontFamilyResolver,
            text = twoLines,
            maxLines = 2
        ).height
    }
    val lineHeight = firstTwoLinesHeight - firstLineHeight
    val precomputedMinLinesHeight = firstLineHeight + lineHeight * (minLines - 1)

    Modifier.heightIn(
        min = with(density) { precomputedMinLinesHeight.toDp() }
    )
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

