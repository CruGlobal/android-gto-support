package org.ccci.gto.android.common.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlPullParserUtils {
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
                default:
                    // do nothing
            }
        }
    }

    /**
     * Similar to {@link XmlPullParser#require(int, String, String)}, but tests to see if the current node name matches
     * any of the provided names.
     *
     * @param parser
     * @param names
     * @throws XmlPullParserException
     */
    public static void requireAnyName(@NonNull final XmlPullParser parser, @NonNull final String... names)
            throws XmlPullParserException {
        final String nodeName = parser.getName();
        if (nodeName != null) {
            for (final String name : names) {
                if (name != null && name.equals(nodeName)) {
                    return;
                }
            }
        }

        throw new XmlPullParserException("expected: " + TextUtils.join("|", names), parser, null);
    }
}
