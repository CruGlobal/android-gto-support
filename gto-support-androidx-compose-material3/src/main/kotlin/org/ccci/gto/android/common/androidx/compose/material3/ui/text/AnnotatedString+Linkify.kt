package org.ccci.gto.android.common.androidx.compose.material3.ui.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.util.LinkifyCompat.LinkifyMask
import org.ccci.gto.android.common.androidx.compose.ui.text.addUriAnnotations

private val DefaultLinkStyle @Composable get() = SpanStyle(
    color = MaterialTheme.colorScheme.primary,
    textDecoration = TextDecoration.Underline
)

@Composable
fun String.addUriAnnotations(@LinkifyMask mask: Int) = addUriAnnotations(mask, DefaultLinkStyle)

@Composable
fun AnnotatedString.addUriAnnotations(@LinkifyMask mask: Int) = addUriAnnotations(mask, DefaultLinkStyle)

@Composable
private fun AnnotatedString.Builder.addUriAnnotations(@LinkifyMask mask: Int) =
    addUriAnnotations(mask, DefaultLinkStyle)
