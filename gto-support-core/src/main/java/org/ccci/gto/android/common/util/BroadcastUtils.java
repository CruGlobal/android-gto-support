package org.ccci.gto.android.common.util;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.PatternMatcher;
import androidx.annotation.NonNull;

public final class BroadcastUtils {
    public static void addDataUri(@NonNull final IntentFilter filter, @NonNull final Uri uri) {
        addDataUri(filter, uri, PatternMatcher.PATTERN_LITERAL);
    }

    public static void addDataUri(@NonNull final IntentFilter filter, @NonNull final Uri uri, final int type) {
        final String scheme = uri.getScheme();
        if (scheme != null) {
            filter.addDataScheme(scheme);
        }
        final String host = uri.getHost();
        if (host != null) {
            filter.addDataAuthority(host, null);
        }
        final String path = uri.getPath();
        if (path != null) {
            filter.addDataPath(path, type);
        }
    }
}
