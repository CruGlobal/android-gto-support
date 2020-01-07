package org.ccci.gto.android.common.api;

/**
 * @deprecated Since v3.3.0, use gto-support-api-okhttp3 for building API clients instead.
 */
@Deprecated
public class ApiException extends Exception {
    private static final long serialVersionUID = 4603497556745452379L;

    public ApiException() {
        super();
    }

    public ApiException(final Throwable throwable) {
        super(throwable);
    }
}
