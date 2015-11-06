package org.ccci.gto.android.common.api;

import android.support.annotation.NonNull;

import com.google.common.io.Closer;

import org.ccci.gto.android.common.api.AbstractApi.Request.MediaType;
import org.ccci.gto.android.common.api.AbstractApi.Request.Parameter;
import org.ccci.gto.android.common.util.IOUtils;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class MultipartFormOutputWriter implements Closeable {
    private static final String PREFIX = "--";
    private static final String NEWLINE = "\r\n";

    @NonNull
    private final DataOutputStream mOut;
    @NonNull
    private final String mBoundary;

    MultipartFormOutputWriter(@NonNull final OutputStream os, @NonNull final String boundary) {
        mOut = new DataOutputStream(os);
        mBoundary = boundary;
    }

    void writeParameter(@NonNull final Parameter param) throws IOException {
        if (param.mFile != null) {
            writeFileParameter(param);
        } else {
            writeSimpleParameter(param);
        }
    }

    private void writeSimpleParameter(@NonNull final Parameter param) throws IOException {
        // write boundary
        mOut.writeBytes(PREFIX);
        mOut.writeBytes(mBoundary);
        mOut.writeBytes(NEWLINE);

        // write content header
        mOut.writeBytes("Content-Disposition: form-data; name=\"" + param.mName + "\"");
        mOut.writeBytes(NEWLINE);
        mOut.writeBytes(NEWLINE);

        // write content
        mOut.writeBytes(param.mValue != null ? param.mValue : "");
        mOut.writeBytes(NEWLINE);
    }

    private void writeFileParameter(@NonNull final Parameter param) throws IOException {
        // write boundary
        mOut.writeBytes(PREFIX);
        mOut.writeBytes(mBoundary);
        mOut.writeBytes(NEWLINE);

        // write content header
        String fileName = param.mFileName;
        if (fileName == null && param.mFile != null) {
            fileName = param.mFile.getName();
        }
        if (fileName == null) {
            fileName = "";
        }
        mOut.writeBytes("Content-Disposition: form-data; name=\"" + param.mName + "\"; filename=\"" + fileName + "\"");
        mOut.writeBytes(NEWLINE);
        final MediaType type = param.mType != null ? param.mType : MediaType.APPLICATION_OCTET_STREAM;
        mOut.writeBytes("Content-Type: " + type.mType);
        mOut.writeBytes(NEWLINE);
        mOut.writeBytes(NEWLINE);

        // write file content
        if (param.mFile != null) {
            final Closer closer = Closer.create();
            try {
                final InputStream in = closer.register(new FileInputStream(param.mFile));
                IOUtils.copy(in, mOut);
            } catch (final Throwable t) {
                throw closer.rethrow(t);
            } finally {
                closer.close();
            }
        }
        mOut.writeBytes(NEWLINE);
    }

    void finish() throws IOException {
        // write final boundary
        mOut.writeBytes(PREFIX);
        mOut.writeBytes(mBoundary);
        mOut.writeBytes(PREFIX);
        mOut.writeBytes(NEWLINE);
        mOut.flush();
    }

    @Override
    public void close() throws IOException {
        mOut.close();
    }
}
