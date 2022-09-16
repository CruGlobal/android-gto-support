package org.ccci.gto.android.common.okta.oidc.storage.security

import android.content.Context
import android.os.Build
import com.okta.oidc.storage.security.DefaultEncryptionManager
import java.util.Locale

fun createDefaultEncryptionManager(context: Context): DefaultEncryptionManager {
    try {
        return DefaultEncryptionManager(context)
    } catch (e: Exception) {
        when {
            // Android 6 bug with RTL locales
            // see: https://issuetracker.google.com/issues/37095309
            Build.VERSION.SDK_INT == Build.VERSION_CODES.M && e.cause.isUnparseableDateException() -> {
                // HACK: workaround the exception by changing the locale to English temporarily
                val initialLocale = Locale.getDefault()
                try {
                    Locale.setDefault(Locale.ENGLISH)
                    return DefaultEncryptionManager(context)
                } catch (_: Exception) {
                } finally {
                    Locale.setDefault(initialLocale)
                }
            }
        }
        throw e
    }
}

private fun Throwable?.isUnparseableDateException() =
    this is IllegalArgumentException && message?.contains("Unparseable date") == true
