package org.ccci.gto.android.common.api.okhttp3;

import java.net.ProtocolException;

public class SessionApiException extends ProtocolException {
    public SessionApiException() {
    }

    public SessionApiException(final Throwable cause) {
        super();
        initCause(cause);
    }
}
