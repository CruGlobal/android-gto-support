package org.ccci.gto.android.common.androidx.compose.material3

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow

// NOTE: this is hopefully temporary until a ClickableText composable is added to Material3
@Composable
@Deprecated("Since v4.2.3, use a Text composable that takes an AnnotatedString with LinkAnnotations instead.")
fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontFamily: FontFamily? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    onClick: (Int) -> Unit,
) {
    // logic copied from the compose-foundation ClickableText
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { layoutResult?.getOffsetForPosition(it)?.let { onClick(it) } }
    }

    Text(
        text = text,
        modifier = modifier.then(pressIndicator),
        color = color,
        fontFamily = fontFamily,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult = it
            onTextLayout(it)
        },
        style = style,
    )
}
