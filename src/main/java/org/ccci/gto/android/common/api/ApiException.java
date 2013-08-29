package org.ccci.gto.android.common.api;

public class ApiException extends Exception {
    private static final long serialVersionUID = 4603497556745452379L;

    public ApiException() {
        super();
    }

    public ApiException(final Throwable throwable) {
        super(throwable);
    }
}
