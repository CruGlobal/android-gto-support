package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class HttpHeaderUtils {
    public static Challenge parseChallenge(@NonNull final String challenge) {
        // separate the scheme from the params
        final String[] parts = challenge.trim().split("[ \t]", 2);
        final String scheme = parts[0].trim();

        // parse any attributes
        final Map<String, String> params = new HashMap<>();
        if (parts.length > 1) {
            return new Challenge(scheme, Parser.parseElements(parts[1], new Parser.Cursor()));
        }

        // return the parsed challenge
        return new Challenge(scheme, null);
    }

    static class NameValuePair {
        @NonNull
        final String name;
        @Nullable
        final String value;

        NameValuePair(@NonNull final String name, @Nullable final String value) {
            this.name = name;
            this.value = value;
        }
    }

    static final class HeaderElement extends NameValuePair {
        @NonNull
        final NameValuePair[] params;

        HeaderElement(@NonNull final String name, @Nullable final String value, @NonNull NameValuePair[] params) {
            super(name, value);
            this.params = params;
        }
    }

    public static final class Challenge {
        @NonNull
        @Deprecated
        public final String scheme;
        @NonNull
        @Deprecated
        public final Map<String, String> params = new HashMap<>();

        private final String mScheme;
        private final Map<String, HeaderElement> mParams = new HashMap<>();

        private Challenge(@NonNull final String scheme, @Nullable final HeaderElement[] params) {
            mScheme = scheme;
            this.scheme = scheme;

            if (params != null) {
                for (final HeaderElement param : params) {
                    mParams.put(param.name.toLowerCase(Locale.US), param);
                    this.params.put(param.name.toLowerCase(Locale.US), param.value);
                }
            }
        }

        @NonNull
        public String getScheme() {
            return mScheme;
        }

        public Collection<HeaderElement> getParameters() {
            return mParams.values();
        }

        @Nullable
        public HeaderElement getParameter(@NonNull final String name) {
            return mParams.get(name.toLowerCase(Locale.US));
        }

        @Nullable
        public String getParameterValue(@NonNull final String name) {
            final HeaderElement param = getParameter(name);
            return param != null ? param.value : null;
        }
    }

    /**
     * Header parser. Originally sourced from BasicHeaderValueParser in Apache HttpClient.
     */
    static final class Parser {
        private static final char PARAM_DELIMITER = ';';
        private static final char ELEM_DELIMITER = ',';
        private static final char[] ALL_DELIMITERS = new char[] {PARAM_DELIMITER, ELEM_DELIMITER};

        // HTTP whitespace chars
        private static final int CR = 13; // <US-ASCII CR, carriage return (13)>
        private static final int LF = 10; // <US-ASCII LF, linefeed (10)>
        private static final int SP = 32; // <US-ASCII SP, space (32)>
        private static final int HT = 9;  // <US-ASCII HT, horizontal-tab (9)>

        static final class Cursor {
            int pos = 0;
        }

        @NonNull
        private static HeaderElement[] parseElements(@NonNull final String buffer, @NonNull final Cursor cursor) {
            final List<HeaderElement> elements = new ArrayList<>();
            while (cursor.pos < buffer.length()) {
                HeaderElement element = parseHeaderElement(buffer, cursor);
                if (!(element.name.length() == 0 && element.value == null)) {
                    elements.add(element);
                }
            }
            return elements.toArray(new HeaderElement[elements.size()]);
        }

        @NonNull
        private static HeaderElement parseHeaderElement(@NonNull final String buffer, @NonNull final Cursor cursor) {
            final NameValuePair nvp = parseNameValuePair(buffer, cursor);
            NameValuePair[] params = new NameValuePair[0];
            if (cursor.pos < buffer.length()) {
                char ch = buffer.charAt(cursor.pos - 1);
                if (ch != ELEM_DELIMITER) {
                    params = parseParameters(buffer, cursor);
                }
            }
            return new HeaderElement(nvp.name, nvp.value, params);
        }

        @NonNull
        private static NameValuePair[] parseParameters(@NonNull final String buffer, @NonNull final Cursor cursor) {
            int pos = cursor.pos;
            final int indexTo = buffer.length();

            while (pos < indexTo) {
                char ch = buffer.charAt(pos);
                if (isHttpWhitespace(ch)) {
                    pos++;
                } else {
                    break;
                }
            }
            cursor.pos = pos;
            if (cursor.pos >= buffer.length()) {
                return new NameValuePair[] {};
            }

            final List<NameValuePair> params = new ArrayList<>();
            while (cursor.pos < buffer.length()) {
                params.add(parseNameValuePair(buffer, cursor));
                char ch = buffer.charAt(cursor.pos - 1);
                if (ch == ELEM_DELIMITER) {
                    break;
                }
            }

            return params.toArray(new NameValuePair[params.size()]);
        }

        private static NameValuePair parseNameValuePair(@NonNull final String buffer, @NonNull final Cursor cursor) {
            return parseNameValuePair(buffer, cursor, ALL_DELIMITERS);
        }

        @NonNull
        private static NameValuePair parseNameValuePair(@NonNull final String buffer, @NonNull final Cursor cursor,
                                                        @NonNull final char[] delimiters) {
            final int indexFrom = cursor.pos;
            final int indexTo = buffer.length();

            int pos = cursor.pos;

            // Find name
            boolean terminated = false;
            while (pos < indexTo) {
                char ch = buffer.charAt(pos);
                if (ch == '=') {
                    break;
                }
                if (isOneOf(ch, delimiters)) {
                    terminated = true;
                    break;
                }
                pos++;
            }

            final String name;
            if (pos == indexTo) {
                terminated = true;
                name = buffer.substring(indexFrom, indexTo).trim();
            } else {
                name = buffer.substring(indexFrom, pos).trim();
                pos++;
            }

            if (terminated) {
                cursor.pos = pos;
                return new NameValuePair(name, null);
            }

            // Find value
            int i1 = pos;

            boolean qouted = false;
            boolean escaped = false;
            while (pos < indexTo) {
                char ch = buffer.charAt(pos);
                if (ch == '"' && !escaped) {
                    qouted = !qouted;
                }
                if (!qouted && !escaped && isOneOf(ch, delimiters)) {
                    terminated = true;
                    break;
                }
                if (escaped) {
                    escaped = false;
                } else {
                    escaped = qouted && ch == '\\';
                }
                pos++;
            }

            int i2 = pos;
            // Trim leading white spaces
            while (i1 < i2 && (isHttpWhitespace(buffer.charAt(i1)))) {
                i1++;
            }
            // Trim trailing white spaces
            while ((i2 > i1) && (isHttpWhitespace(buffer.charAt(i2 - 1)))) {
                i2--;
            }
            // Strip away quotes if necessary
            if (((i2 - i1) >= 2)
                    && (buffer.charAt(i1) == '"')
                    && (buffer.charAt(i2 - 1) == '"')) {
                i1++;
                i2--;
            }
            final String value = buffer.substring(i1, i2);
            if (terminated) {
                pos++;
            }
            cursor.pos = pos;
            return new NameValuePair(name, value);
        }

        private static boolean isHttpWhitespace(final char ch) {
            return ch == SP || ch == HT || ch == CR || ch == LF;
        }

        private static boolean isOneOf(final char ch, final char[] chs) {
            if (chs != null) {
                for (int i = 0; i < chs.length; i++) {
                    if (ch == chs[i]) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
