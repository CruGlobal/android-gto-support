package org.ccci.gto.android.common.util.xmlpull

import org.xmlpull.v1.XmlPullParser

/**
 *  Skip the current XML tag (and all of it's children)
 */
fun XmlPullParser.skipTag() {
    require(XmlPullParser.START_TAG, null, null)

    // loop until we process all nested tags
    var depth = 1
    while (depth > 0) {
        when (next()) {
            XmlPullParser.START_TAG -> depth++
            XmlPullParser.END_TAG -> depth--
        }
    }
}
