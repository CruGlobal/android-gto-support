package com.okta.authfoundation.credential;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import com.okta.authfoundation.client.OidcClient;

import org.ccci.gto.android.common.util.lang.ClassKt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("KotlinInternalInJava")
public class SharedPreferencesTokenStorageInternals {
    @RequiresApi(Build.VERSION_CODES.M)
    public static SharedPreferencesTokenStorage create(
            @NonNull final OidcClient client,
            @NonNull final Context context,
            @NonNull KeyGenParameterSpec keyGenParameterSpec
    ) {
        return new SharedPreferencesTokenStorage(
                client.getConfiguration().getJson(),
                client.getConfiguration().getIoDispatcher(),
                client.getConfiguration().getEventCoordinator(),
                context,
                keyGenParameterSpec
        );
    }

    @Nullable
    @RequiresApi(Build.VERSION_CODES.M)
    public static SharedPreferences getSharedPreferences(SharedPreferencesTokenStorage storage) throws Throwable {
        Method method = ClassKt.getDeclaredMethodOrNull(SharedPreferencesTokenStorage.class, "getSharedPreferences()");
        if (method != null) {
            try {
                return (SharedPreferences) method.invoke(storage);
            } catch (InvocationTargetException e) {
                throw e.getCause() != null ? e.getCause() : e;
            }
        }
        return null;
    }
}
