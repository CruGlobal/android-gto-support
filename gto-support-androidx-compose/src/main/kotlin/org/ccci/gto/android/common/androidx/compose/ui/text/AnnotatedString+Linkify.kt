package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.util.LinkifyCompat
import androidx.core.text.util.LinkifyCompat.LinkifyMask

fun String.addLinks(
    @LinkifyMask mask: Int,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener? = null,
) = AnnotatedString.Builder(this).apply {
    val (spannable, spans) = getUrlSpans(this@addLinks, mask)
    addLinks(spannable, spans, styles, linkInteractionListener)
}.toAnnotatedString()

fun AnnotatedString.addLinks(
    @LinkifyMask mask: Int,
    styles: TextLinkStyles?,
    linkInteractionListener: LinkInteractionListener? = null,
): AnnotatedString {
    val (spannable, spans) = getUrlSpans(this, mask)
    return when {
        spans.isEmpty() -> this
        else -> buildAnnotatedString {
            append(this@addLinks)
            addLinks(spannable, spans, styles, linkInteractionListener)
        }
    }
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
