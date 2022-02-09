package com.okta.oidc.storage.security

import android.content.Context
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.stubbing.OngoingStubbing
import org.mockito.verification.VerificationMode

class EncryptionManagerFactoryMock internal constructor(private val mock: MockedStatic<EncryptionManagerFactory>) :
    AutoCloseable by mock {
    fun whenCreateEncryptionManager(
        context: Context? = any(),
        keyStoreName: String? = any(),
        keyAlias: String? = any(),
        isAuthenticateUserRequired: Boolean = any(),
        userAuthenticationValidityDurationSeconds: Int = any(),
        initCipherOnCreate: Boolean = any()
    ): OngoingStubbing<EncryptionManager> = mock.`when` {
        EncryptionManagerFactory.createEncryptionManager(
            context,
            keyStoreName,
            keyAlias,
            isAuthenticateUserRequired,
            userAuthenticationValidityDurationSeconds,
            initCipherOnCreate
        )
    }

    fun verifyCreateEncryptionManager(mode: VerificationMode = times(1)) = mock.verify(
        { EncryptionManagerFactory.createEncryptionManager(any(), any(), any(), any(), any(), any()) },
        mode
    )

    fun clearInvocations() = mock.clearInvocations()
}

fun mockEncryptionManagerFactory(): EncryptionManagerFactoryMock =
    EncryptionManagerFactoryMock(mockStatic(EncryptionManagerFactory::class.java))
