package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.SpannableString
import android.text.style.URLSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.core.text.util.LinkifyCompat
import androidx.core.text.util.LinkifyCompat.LinkifyMask

private const val TAG_URI = "Uri"

fun String.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) = with(AnnotatedString.Builder(this)) {
    addUriAnnotations(mask, linkStyle)
    toAnnotatedString()
}

fun AnnotatedString.addUriAnnotations(
    @LinkifyMask mask: Int,
    linkStyle: SpanStyle?
) = with(AnnotatedString.Builder(this)) {
    addUriAnnotations(mask, linkStyle)
    toAnnotatedString()
}

fun AnnotatedString.Builder.addUriAnnotations(@LinkifyMask mask: Int, linkStyle: SpanStyle?) {
    val spannable = SpannableString(toAnnotatedString().text)
    LinkifyCompat.addLinks(spannable, mask)
    spannable.getSpans(0, spannable.length, URLSpan::class.java).forEach {
        val spanStart = spannable.getSpanStart(it)
        val spanEnd = spannable.getSpanEnd(it)
        addUriAnnotation(it.url, spanStart, spanEnd)
        if (linkStyle != null) addStyle(linkStyle, spanStart, spanEnd)
    }
}

private fun AnnotatedString.Builder.addUriAnnotation(url: String, start: Int, end: Int) =
    addStringAnnotation(TAG_URI, url, start, end)

fun AnnotatedString.getUriAnnotations(start: Int, end: Int) = getStringAnnotations(TAG_URI, start, end)
