package org.ccci.gto.android.common.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.ccci.gto.android.common.util.xmlpull.XmlPullParserKt;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlPullParserUtils {
    /**
     * Skip the current XML tag (and all of it's children)
     * @deprecated Since v3.6.1, Use the Kotlin extension function instead.
     */
    @Deprecated
    public static void skipTag(@NonNull final XmlPullParser parser) throws XmlPullParserException, IOException {
        XmlPullParserKt.skipTag(parser);
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
