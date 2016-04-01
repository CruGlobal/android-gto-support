package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlPullParserUtils {
    /**
     * safely call nextText on all versions of Android.
     *
     * @param parser the current parser
     * @return the next text node from the XmlPullParser
     * @throws IOException
     * @throws XmlPullParserException
     * @see <a href="http://android-developers.blogspot.com/2011/12/watch-out-for-xmlpullparsernexttext.html">Watch out for XmlPullParser.nextText()</a>
     */
    public static String safeNextText(@NonNull final XmlPullParser parser) throws IOException, XmlPullParserException {
        final String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            // work around a bug pre-ICS where nextText() didn't consume the text event
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skip the current XML tag (and all of it's children)
     *
     * @param parser the parser for the xml document being processed
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static void skipTag(@NonNull final XmlPullParser parser) throws XmlPullParserException, IOException {
        // require that we are currently at the start of a tag
        parser.require(XmlPullParser.START_TAG, null, null);

        // loop until we process all nested tags
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
}
