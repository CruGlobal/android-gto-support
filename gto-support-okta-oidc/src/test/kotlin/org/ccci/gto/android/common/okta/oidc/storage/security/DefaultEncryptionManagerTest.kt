package org.ccci.gto.android.common.okta.oidc.storage.security

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.okta.oidc.storage.security.DefaultEncryptionManager
import com.okta.oidc.storage.security.mockEncryptionManagerFactory
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class DefaultEncryptionManagerTest {
    private val context: Context = mock()

    @Test
    @Config(sdk = [Build.VERSION_CODES.M])
    fun `createDefaultEncryptionManager() - Android 6 Locale bug`() {
        mockEncryptionManagerFactory().use { factory ->
            val manager: DefaultEncryptionManager = mock()
            factory.whenCreateEncryptionManager().doAnswer {
                when (Locale.getDefault().language) {
                    "ar", "fa" -> throw RuntimeException(
                        "Failed generate keys.",
                        IllegalArgumentException("invalid date string: Unparseable date: \"g``a`a``````GMT+00:00\"")
                    )

                    else -> manager
                }
            }

            listOf("ar", "fa", "fa-IR").forEach {
                val initialLocale = Locale.getDefault()

                Locale.setDefault(Locale.forLanguageTag(it))
                createDefaultEncryptionManager(context)
                factory.verifyCreateEncryptionManager(times(2))

                Locale.setDefault(initialLocale)
                factory.clearInvocations()
            }
        }
    }
}
