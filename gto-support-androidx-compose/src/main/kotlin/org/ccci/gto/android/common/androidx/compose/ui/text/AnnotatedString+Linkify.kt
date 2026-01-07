package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.util.LinkifyCompat
import androidx.core.text.util.LinkifyCompat.LinkifyMask

@Deprecated("Since v4.2.3, use LinkAnnotations instead.")
private const val TAG_URI = "uri"

@Deprecated("Since v4.2.3, use addLinks() instead.")
fun String.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) = AnnotatedString.Builder(this).apply {
    val (spannable, spans) = getUrlSpans(this@addUriAnnotations, mask)
    addUrlSpans(spannable, spans, linkStyle)
}.toAnnotatedString()

fun String.addLinks(
    @LinkifyMask mask: Int,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener? = null,
) = AnnotatedString.Builder(this).apply {
    val (spannable, spans) = getUrlSpans(this@addLinks, mask)
    addLinks(spannable, spans, styles, linkInteractionListener)
}.toAnnotatedString()

@Deprecated("Since v4.2.3, use addLinks() instead.")
fun AnnotatedString.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?): AnnotatedString {
    val (spannable, spans) = getUrlSpans(this, mask)
    if (spans.isEmpty()) return this
    return buildAnnotatedString {
        append(this@addUriAnnotations)
        addUrlSpans(spannable, spans, linkStyle)
    }
}

fun AnnotatedString.addLinks(
    @LinkifyMask mask: Int,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener? = null,
): AnnotatedString {
    val (spannable, spans) = getUrlSpans(this, mask)
    if (spans.isEmpty()) return this
    return buildAnnotatedString {
        append(this@addLinks)
        addLinks(spannable, spans, styles, linkInteractionListener)
    }
}

@Deprecated("Since v4.2.3, use addLinks() instead.")
fun AnnotatedString.Builder.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) {
    val (spannable, spans) = getUrlSpans(toAnnotatedString().text, mask)
    addUrlSpans(spannable, spans, linkStyle)
}

fun AnnotatedString.Builder.addLinks(
    @LinkifyMask mask: Int,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener? = null,
) {
    val (spannable, spans) = getUrlSpans(toAnnotatedString().text, mask)
    addLinks(spannable, spans, styles, linkInteractionListener)
}

private fun getUrlSpans(string: CharSequence, @LinkifyMask mask: Int): Pair<SpannableString, Array<URLSpan>> {
    val spannable = SpannableString(string).apply { LinkifyCompat.addLinks(this, mask) }
    return spannable to spannable.getSpans(0, spannable.length, URLSpan::class.java)
}

@Deprecated("Since v4.2.3, use addLinks() instead.")
private fun AnnotatedString.Builder.addUrlSpans(
    spannable: SpannableString,
    spans: Array<URLSpan>,
    linkStyle: SpanStyle?,
) {
    spans.forEach {
        val spanStart = spannable.getSpanStart(it)
        val spanEnd = spannable.getSpanEnd(it)
        addUriAnnotation(it.url, spanStart, spanEnd)
        if (linkStyle != null) addStyle(linkStyle, spanStart, spanEnd)
    }
}

private fun AnnotatedString.Builder.addLinks(
    spannable: SpannableString,
    spans: Array<URLSpan>,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener?,
) {
    spans.forEach {
        val spanStart = spannable.getSpanStart(it)
        val spanEnd = spannable.getSpanEnd(it)
        addLink(LinkAnnotation.Url(it.url, styles, linkInteractionListener), spanStart, spanEnd)
    }
}

@Deprecated("Since v4.2.3, use LinkAnnotations instead.")
private fun AnnotatedString.Builder.addUriAnnotation(uri: String, start: Int, end: Int) =
    addStringAnnotation(TAG_URI, uri, start, end)
@Deprecated("Since v4.2.3, use LinkAnnotations instead.")
fun AnnotatedString.getUriAnnotations(start: Int, end: Int) = getStringAnnotations(TAG_URI, start, end)
