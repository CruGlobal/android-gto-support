package org.ccci.gto.android.common.okta.oidc.storage.security

import android.content.Context
import com.okta.oidc.storage.security.EncryptionManager
import javax.crypto.Cipher

object NoopEncryptionManager : EncryptionManager {
    override fun isHardwareBackedKeyStore() = false
    override fun isUserAuthenticatedOnDevice() = true

    override fun encrypt(value: String?) = value
    override fun decrypt(value: String?) = value
    override fun getHashed(value: String?) = value

    override fun isValidKeys() = false
    override fun recreateKeys(context: Context?) = Unit
    override fun removeKeys() = Unit

    override fun setCipher(cipher: Cipher?) = Unit
    override fun getCipher() = null
    override fun recreateCipher() = Unit
}
