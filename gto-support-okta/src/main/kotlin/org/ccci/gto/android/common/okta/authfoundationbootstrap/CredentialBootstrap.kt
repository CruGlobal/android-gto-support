package org.ccci.gto.android.common.okta.authfoundationbootstrap

import com.okta.authfoundation.credential.Credential
import com.okta.authfoundation.credential.TokenStorage
import com.okta.authfoundation.credential.storage
import com.okta.authfoundationbootstrap.CredentialBootstrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.ccci.gto.android.common.okta.authfoundation.credential.ChangeAwareTokenStorage
import org.ccci.gto.android.common.okta.authfoundation.credential.changeFlow

@OptIn(ExperimentalCoroutinesApi::class)
fun CredentialBootstrap.defaultCredentialFlow(
    storage: TokenStorage =
        checkNotNull(credentialDataSource.storage) { "Unable to access TokenStorage from the CredentialDataSource" }
): Flow<Credential> {
    check(storage is ChangeAwareTokenStorage) { "credentialDataSource is not using a ChangeAwareTokenStorage" }
    return storage.changeFlow().mapLatest { defaultCredential() }
}
