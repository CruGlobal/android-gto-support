package org.ccci.gto.android.common.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public final class IOUtils {
    // 640K should be enough for anybody -Bill Gates
    // (Bill Gates never actually said this)
    private static final int DEFAULT_BUFFER_SIZE = 640 * 1024;
    private static final int EOF = -1;

    public static void closeQuietly(final Closeable handle) {
        if (handle != null) {
            try {
                handle.close();
            } catch (final IOException e) {
                // suppress the error
            }
        }
    }

    public static void closeQuietly(final HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    public static long copy(final InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (EOF != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String readString(final InputStream in) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), DEFAULT_BUFFER_SIZE);
        final StringBuilder out = new StringBuilder();
        final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n;
        while (EOF != (n = reader.read(buffer, 0, buffer.length))) {
            out.append(buffer, 0, n);
        }
        return out.toString();
    }
}
