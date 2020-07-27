package org.ccci.gto.android.common.util.xmlpull

import android.util.Xml
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParser

@RunWith(AndroidJUnit4::class)
class XmlPullParserTest {
    @Test
    fun verifySkipTagRecursive() {
        val parser = getXmlParserForResource("skip_tag_recursive.xml")
        while (parser.next() != XmlPullParser.START_TAG && parser.name != "n1") Unit
        parser.skipTag()
        assertEquals(XmlPullParser.END_TAG, parser.eventType)
        assertEquals("n1", parser.name)
    }
}

private fun Any.getXmlParserForResource(name: String): XmlPullParser = with(getInputStreamForResource(name)) {
    Xml.newPullParser().also {
        it.setInput(this, "UTF-8")
        it.nextTag()
    }
}

private fun Any.getInputStreamForResource(name: String): InputStream = this::class.java.getResourceAsStream(name)!!
