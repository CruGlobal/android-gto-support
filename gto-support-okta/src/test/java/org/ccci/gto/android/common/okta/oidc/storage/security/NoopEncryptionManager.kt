package org.ccci.gto.android.common.okta.oidc.storage.security

import android.content.Context
import com.okta.oidc.storage.security.EncryptionManager
import javax.crypto.Cipher

object NoopEncryptionManager : EncryptionManager {
    override fun isHardwareBackedKeyStore() = false
    override fun encrypt(value: String?) = value
    override fun decrypt(value: String?) = value
    override fun getHashed(value: String?) = value

    override fun recreateCipher() = TODO("Not yet implemented")
    override fun setCipher(cipher: Cipher?) = TODO("Not yet implemented")
    override fun getCipher() = TODO("Not yet implemented")
    override fun removeKeys() = TODO("Not yet implemented")
    override fun recreateKeys(context: Context?) = TODO("Not yet implemented")
    override fun isValidKeys() = TODO("Not yet implemented")
    override fun isUserAuthenticatedOnDevice() = TODO("Not yet implemented")
}
