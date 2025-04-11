package org.ccci.gto.android.common.androidx.compose.ui.text

import android.text.util.Linkify
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private const val RAW = "pre-url https://www.example.com post-url"
private const val LINK = "https://www.example.com"

@RunWith(AndroidJUnit4::class)
class AnnotatedStringLinkifyTest {
    private val textLinkStyles = TextLinkStyles()
    private val linkInteractionListener = LinkInteractionListener {}

    @Test
    fun testStringAddLinks() {
        assertLinkAnnotatedString(RAW.addLinks(Linkify.WEB_URLS, textLinkStyles, linkInteractionListener))
    }

    @Test
    fun testAnnotatedStringAddLinks() {
        assertLinkAnnotatedString(
            AnnotatedString(RAW).addLinks(Linkify.WEB_URLS, textLinkStyles, linkInteractionListener)
        )
    }

    @Test
    fun testAnnotatedStringBuilderAddLinks() {
        assertLinkAnnotatedString(
            buildAnnotatedString {
                append(RAW)
                addLinks(Linkify.WEB_URLS, textLinkStyles, linkInteractionListener)
            }
        )
    }

    private fun assertLinkAnnotatedString(string: AnnotatedString) {
        assertEquals(RAW, string.text)
        assertTrue(string.getLinkAnnotations(0, 7).isEmpty())
        assertTrue(string.getLinkAnnotations(string.length - 8, string.length).isEmpty())

        val link = assertIs<LinkAnnotation.Url>(string.getLinkAnnotations(10, 10).single().item)
        assertEquals(LINK, link.url)
        assertSame(textLinkStyles, link.styles)
        assertSame(linkInteractionListener, link.linkInteractionListener)
    }
}
