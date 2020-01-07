package org.ccci.gto.android.common.api;

/**
 * @deprecated Since v3.3.0, use gto-support-api-okhttp3 for building API clients instead.
 */
@Deprecated
public class ApiSocketException extends ApiException {
    private static final long serialVersionUID = 6504089269603588132L;

    public ApiSocketException() {
        super();
    }

    public ApiSocketException(final Throwable throwable) {
        super(throwable);
    }
}
