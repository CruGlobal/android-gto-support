package org.ccci.gto.android.common.okta.authfoundation.credential

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.RequiresApi
import androidx.security.crypto.MasterKeys
import com.okta.authfoundation.client.OidcClient
import com.okta.authfoundation.credential.SharedPreferencesTokenStorageInternals
import com.okta.authfoundation.credential.TokenStorage

@RequiresApi(Build.VERSION_CODES.M)
fun SharedPreferencesTokenStorage(
    client: OidcClient,
    context: Context,
    keyGenParameterSpec: KeyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
): TokenStorage = SharedPreferencesTokenStorageInternals.create(client, context, keyGenParameterSpec)
