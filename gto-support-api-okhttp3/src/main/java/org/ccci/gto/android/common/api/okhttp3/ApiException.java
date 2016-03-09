package org.ccci.gto.android.common.api.okhttp3;

import java.io.IOException;

public class ApiException extends IOException {
    public ApiException() {
    }

    public ApiException(Throwable cause) {
        super(cause);
    }
}
