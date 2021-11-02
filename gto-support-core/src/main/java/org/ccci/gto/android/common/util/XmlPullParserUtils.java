package org.ccci.gto.android.common.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlPullParserUtils {
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
