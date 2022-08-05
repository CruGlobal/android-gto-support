package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.util.LinkifyCompat
import androidx.core.text.util.LinkifyCompat.LinkifyMask

private const val TAG_URI = "uri"

fun String.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) = AnnotatedString.Builder(this).apply {
    val (spannable, spans) = getUrlSpans(this@addUriAnnotations, mask)
    addUrlSpans(spannable, spans, linkStyle)
}.toAnnotatedString()

fun AnnotatedString.addUriAnnotations(
    @LinkifyMask mask: Int,
    linkStyle: SpanStyle?
): AnnotatedString {
    val (spannable, spans) = getUrlSpans(this, mask)
    return when {
        spans.isEmpty() -> this
        else -> buildAnnotatedString {
            append(this@addUriAnnotations)
            addUrlSpans(spannable, spans, linkStyle)
        }
    }
}

fun AnnotatedString.Builder.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) {
    val (spannable, spans) = getUrlSpans(toAnnotatedString().text, mask)
    addUrlSpans(spannable, spans, linkStyle)
}

private fun getUrlSpans(string: CharSequence, @LinkifyMask mask: Int): Pair<SpannableString, Array<URLSpan>> {
    val spannable = SpannableString(string).apply { LinkifyCompat.addLinks(this, mask) }
    return spannable to spannable.getSpans(0, spannable.length, URLSpan::class.java)
}

private fun AnnotatedString.Builder.addUrlSpans(
    spannable: SpannableString,
    spans: Array<URLSpan>,
    linkStyle: SpanStyle?
) {
    spans.forEach {
        val spanStart = spannable.getSpanStart(it)
        val spanEnd = spannable.getSpanEnd(it)
        addUriAnnotation(it.url, spanStart, spanEnd)
        if (linkStyle != null) addStyle(linkStyle, spanStart, spanEnd)
    }
}

private fun AnnotatedString.Builder.addUriAnnotation(uri: String, start: Int, end: Int) =
    addStringAnnotation(TAG_URI, uri, start, end)
fun AnnotatedString.getUriAnnotations(start: Int, end: Int) = getStringAnnotations(TAG_URI, start, end)
