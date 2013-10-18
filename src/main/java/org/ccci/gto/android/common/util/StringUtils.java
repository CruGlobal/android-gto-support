package org.ccci.gto.android.common.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static String join(final String sep, final String... parts) {
        assert sep != null;

        final StringBuilder sb = new StringBuilder(sep.length() * parts.length);
        boolean first = true;
        for (final String part : parts) {
            assert part != null;
            if (!first) {
                sb.append(sep);
            }
            sb.append(part);
            first = false;
        }

        return sb.toString();
    }
}
