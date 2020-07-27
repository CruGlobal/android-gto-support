package org.ccci.gto.android.common.util.xmlpull

import android.util.Xml
import java.io.Closeable
import java.io.InputStream
import java.io.Reader
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class CloseableXmlPullParser private constructor(private val parser: XmlPullParser) :
        XmlPullParser by parser, Closeable {
    constructor() : this(Xml.newPullParser())

    private var input: Closeable? = null

    @Throws(XmlPullParserException::class)
    override fun setInput(reader: Reader?) {
        input = reader
        parser.setInput(reader)
    }

    @Throws(XmlPullParserException::class)
    override fun setInput(inputStream: InputStream, inputEncoding: String?) {
        input = inputStream
        parser.setInput(inputStream, inputEncoding)
    }

    override fun close() {
        input?.close()
    }
}
