package org.ccci.gto.android.common.androidx.compose.material3.ui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.util.LinkifyCompat.LinkifyMask
import org.ccci.gto.android.common.androidx.compose.ui.text.addLinks
import org.ccci.gto.android.common.androidx.compose.ui.text.addUriAnnotations

private val DefaultLinkStyle @Composable get() = SpanStyle(
    color = MaterialTheme.colorScheme.primary,
    textDecoration = TextDecoration.Underline
)

private val DefaultTextLinkStyles @Composable get() = TextLinkStyles(style = DefaultLinkStyle)

@Composable
@Deprecated("Since v4.2.3, use addLinks() instead.")
fun String.addUriAnnotations(@LinkifyMask mask: Int): AnnotatedString {
    val style = DefaultLinkStyle
    return remember(this, mask, style) { addUriAnnotations(mask, style) }
}

@Composable
@Deprecated("Since v4.2.3, use addLinks() instead.")
fun AnnotatedString.addUriAnnotations(@LinkifyMask mask: Int): AnnotatedString {
    val style = DefaultLinkStyle
    return remember(this, mask, style) { addUriAnnotations(mask, style) }
}

@Composable
fun String.addLinks(@LinkifyMask mask: Int, linkInteractionListener: LinkInteractionListener? = null): AnnotatedString {
    val styles = DefaultTextLinkStyles
    return remember(this, mask, styles, linkInteractionListener) { addLinks(mask, styles, linkInteractionListener) }
}

@Composable
fun AnnotatedString.addLinks(
    @LinkifyMask mask: Int,
    linkInteractionListener: LinkInteractionListener? = null,
): AnnotatedString {
    val styles = DefaultTextLinkStyles
    return remember(this, mask, styles, linkInteractionListener) { addLinks(mask, styles, linkInteractionListener) }
}
