package org.ccci.gto.android.common.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleAccountAuthenticator extends AbstractAccountAuthenticator {
    public SimpleAccountAuthenticator(@NonNull final Context context) {
        super(context);
    }

    @Override
    public Bundle editProperties(@NonNull final AccountAuthenticatorResponse response, final String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(@NonNull final AccountAuthenticatorResponse response, @NonNull final String accountType,
                             @Nullable final String authTokenType, @Nullable final String[] requiredFeatures,
                             @Nullable final Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle confirmCredentials(@NonNull final AccountAuthenticatorResponse response,
                                     @NonNull final Account account, @Nullable final Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(@NonNull final AccountAuthenticatorResponse response, @NonNull final Account account,
                               @NonNull final String authTokenType, @Nullable final Bundle options)
            throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(@NonNull final String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(@NonNull final AccountAuthenticatorResponse response,
                                    @NonNull final Account account, @Nullable final String authTokenType,
                                    @Nullable final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(@NonNull final AccountAuthenticatorResponse response, @NonNull final Account account,
                              @NonNull final String[] features) throws NetworkErrorException {
        return null;
    }
}
