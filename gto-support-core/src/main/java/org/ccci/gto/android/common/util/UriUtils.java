package org.ccci.gto.android.common.util;

import android.net.Uri;
import android.net.Uri.Builder;
import androidx.annotation.NonNull;

import java.util.regex.Pattern;

public final class UriUtils {
    @NonNull
    public static Builder removeQueryParams(@NonNull final Builder uri, final String... keys) {
        if (keys.length > 0) {
            final Uri uriTemp = uri.build();
            String query = uriTemp.getEncodedQuery();
            if (query != null) {
                for (final String key : keys) {
                    // remove all values for key from query
                    final String encodedKey = Uri.encode(key);
                    query = query.replaceAll("&?" + Pattern.quote(encodedKey) + "=[^&]*&?", "&");
                }

                // strip leading/trailing &
                while (query.startsWith("&")) {
                    query = query.substring(1);
                }
                while (query.endsWith("&")) {
                    query = query.substring(0, query.length() - 1);
                }

                // replace query
                uri.encodedQuery(query);
            }
        }

        // return the Builder to allow chaining
        return uri;
    }

    @NonNull
    public static Builder replaceQueryParam(@NonNull final Builder uri, @NonNull final String key,
                                            final String... values) {
        // remove all values for key from query
        removeQueryParams(uri, key);

        // append all specified values
        for (final String value : values) {
            uri.appendQueryParameter(key, value);
        }

        // return the Builder to allow chaining
        return uri;
    }
}
