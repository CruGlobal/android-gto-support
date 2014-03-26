package org.ccci.gto.android.common.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public final class XmlUtils {
    /**
     * safely call nextText on all versions of Android.
     *
     * @param parser the current parser
     * @return the next text node from the XmlPullParser
     * @throws IOException
     * @throws XmlPullParserException
     * @see <a href="http://android-developers.blogspot.com/2011/12/watch-out-for-xmlpullparsernexttext.html">Watch out for XmlPullParser.nextText()</a>
     */
    public static String safeNextText(final XmlPullParser parser) throws IOException, XmlPullParserException {
        final String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        return result;
    }
}
