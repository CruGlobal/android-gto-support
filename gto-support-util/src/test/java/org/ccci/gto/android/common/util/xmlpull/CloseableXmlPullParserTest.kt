package org.ccci.gto.android.common.util.xmlpull

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import java.io.InputStream
import java.io.Reader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CloseableXmlPullParserTest {
    private lateinit var input: InputStream
    private lateinit var reader: Reader

    @Before
    fun setup() {
        input = mock()
        reader = mock()
    }

    @Test
    fun testInputStreamClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(input, null)
        parser.close()
        verify(input).close()
    }

    @Test
    fun testInputStreamReplacedNullClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(input, null)
        parser.setInput(null)
        parser.close()
        verify(input, never()).close()
    }

    @Test
    fun testInputStreamReplacedReaderClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(input, null)
        parser.setInput(reader)
        parser.close()
        verify(input, never()).close()
        verify(reader).close()
    }

    @Test
    fun testReaderClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(reader)
        parser.close()
        verify(reader).close()
    }

    @Test
    fun testReaderReplacedNullClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(reader)
        parser.setInput(null)
        parser.close()
        verify(reader, never()).close()
    }

    @Test
    fun testReaderReplacedInputStreamClose() {
        val parser = CloseableXmlPullParser()
        parser.setInput(reader)
        parser.setInput(input, null)
        parser.close()
        verify(input).close()
        verify(reader, never()).close()
    }
}
