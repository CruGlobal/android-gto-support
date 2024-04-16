package org.ccci.gto.android.common.androidx.compose.foundation.text

import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.TextStyle
import org.ccci.gto.android.common.androidx.compose.ui.text.computeHeightForDefaultText

// Logic based on MaxLinesHeightModifier from Android Open Source Project.

/**
 * Constraint the height of the text field so that it vertically occupies at least [minLines]
 * number of lines.
 *
 * This modifier can be deprecated once upstream support for minLines exists.
 * related ticket: https://issuetracker.google.com/issues/122476634
 */
@Deprecated("Since v4.2.1, use the minLines property on Text Composables instead.")
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.minLinesHeight(minLines: Int, textStyle: TextStyle) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "minLinesHeight"
        properties["minLines"] = minLines
        properties["textStyle"] = textStyle
    }
) {
    require(minLines >= 0) { "minLines must be greater than or equal to 0" }
    if (minLines == 0) return@composed Modifier

    // Difference between the height of two lines paragraph and one line paragraph gives us
    // an approximation of height of one line
    val firstLineHeight = computeHeightForDefaultText(textStyle, 1)
    val firstTwoLinesHeight = computeHeightForDefaultText(textStyle, 2)
    val lineHeight = firstTwoLinesHeight - firstLineHeight
    val precomputedMinLinesHeight = firstLineHeight + lineHeight * (minLines - 1)

    Modifier.heightIn(min = precomputedMinLinesHeight)
}
