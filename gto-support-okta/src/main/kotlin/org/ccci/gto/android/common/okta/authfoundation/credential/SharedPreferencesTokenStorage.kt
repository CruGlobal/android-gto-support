package org.ccci.gto.android.common.okta.authfoundation.credential

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.RequiresApi
import androidx.security.crypto.MasterKeys
import com.okta.authfoundation.client.OidcClient
import com.okta.authfoundation.credential.SharedPreferencesTokenStorageInternals
import com.okta.authfoundation.credential.TokenStorage

@Suppress("ktlint:standard:function-naming")
@RequiresApi(Build.VERSION_CODES.M)
fun SharedPreferencesTokenStorage(
    client: OidcClient,
    context: Context,
    keyGenParameterSpec: KeyGenParameterSpec = MasterKeys.AES256_GCM_SPEC,
    verify: Boolean = false,
): TokenStorage = SharedPreferencesTokenStorageInternals.create(client, context, keyGenParameterSpec)
    .also { if (verify) SharedPreferencesTokenStorageInternals.getSharedPreferences(it) }
