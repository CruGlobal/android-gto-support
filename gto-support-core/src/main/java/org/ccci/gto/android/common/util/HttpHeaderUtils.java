package org.ccci.gto.android.common.util;

import android.support.annotation.NonNull;

import org.apache.http.HeaderElement;
import org.apache.http.message.BasicHeaderValueParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpHeaderUtils {
    public static Challenge parseChallenge(@NonNull final String challenge) {
        // separate the scheme from the params
        final String[] parts = challenge.trim().split("[ \t]", 2);
        final String scheme = parts[0].trim();

        // parse any attributes
        final Map<String, String> params = new HashMap<>();
        if(parts.length > 1) {
            try {
                for (final HeaderElement param : BasicHeaderValueParser.parseElements(parts[1], null)) {
                    params.put(param.getName().toLowerCase(Locale.US), param.getValue());
                }
            } catch (final Exception ignored) {
            }
        }

        // return the parsed challenge
        return new Challenge(scheme, params);
    }

    public static final class Challenge {
        @NonNull
        public final String scheme;
        @NonNull
        public final Map<String, String> params;

        private Challenge(@NonNull final String scheme, @NonNull final Map<String, String> params) {
            this.scheme = scheme;
            this.params = Collections.unmodifiableMap(params);
        }
    }
}
