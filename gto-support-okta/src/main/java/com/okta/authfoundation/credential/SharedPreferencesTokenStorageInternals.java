package com.okta.authfoundation.credential;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import com.okta.authfoundation.client.OidcClient;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SharedPreferencesTokenStorageInternals {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressWarnings("KotlinInternalInJava")
    public static SharedPreferencesTokenStorage create(@NonNull final OidcClient client, @NonNull final Context context, @NonNull KeyGenParameterSpec keyGenParameterSpec) {
        return new SharedPreferencesTokenStorage(
                client.getConfiguration().getJson(),
                client.getConfiguration().getIoDispatcher(),
                client.getConfiguration().getEventCoordinator(),
                context,
                keyGenParameterSpec
        );
    }
}
