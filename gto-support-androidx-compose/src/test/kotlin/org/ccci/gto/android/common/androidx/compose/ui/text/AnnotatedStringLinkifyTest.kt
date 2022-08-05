package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.util.Linkify
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private const val RAW = "pre-url https://www.example.com post-url"

@RunWith(AndroidJUnit4::class)
class AnnotatedStringLinkifyTest {
    private val linkStyle = mockk<SpanStyle>()

    @Test
    fun testStringAddUrlAnnotations() {
        assertUrlAnnotatedString(RAW.addUriAnnotations(Linkify.WEB_URLS, linkStyle))
    }

    @Test
    fun testAnnotatedStringAddUrlAnnotations() {
        assertUrlAnnotatedString(AnnotatedString(RAW).addUriAnnotations(Linkify.WEB_URLS, linkStyle))
    }

    @Test
    fun testAnnotatedStringBuilderAddUrlAnnotations() {
        assertUrlAnnotatedString(
            buildAnnotatedString {
                append(RAW)
                addUriAnnotations(Linkify.WEB_URLS, linkStyle)
            }
        )
    }

    private fun assertUrlAnnotatedString(string: AnnotatedString) {
        assertTrue(string.getUriAnnotations(0, 7).isEmpty())
        assertTrue(string.getUriAnnotations(string.length - 8, string.length).isEmpty())
        val annotation = string.getUriAnnotations(10, 10).single()
        assertEquals("https://www.example.com", annotation.item)
        assertEquals(1, string.spanStyles.size)
        val spanStyle = string.spanStyles.single()
        assertEquals(annotation.start, spanStyle.start)
        assertEquals(annotation.end, spanStyle.end)
        assertSame(linkStyle, spanStyle.item)
    }
}
