package org.ccci.gto.android.common.api.okhttp3.interceptor

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response
import org.ccci.gto.android.common.api.okhttp3.SessionApiException

class SessionRetryInterceptor @JvmOverloads constructor(
    private val sessionInterceptor: SessionInterceptor<*>? = null,
    attempts: Int = ATTEMPTS_DEFAULT,
) : Interceptor {
    companion object {
        private const val ATTEMPTS_DEFAULT = 3
        internal const val ATTEMPTS_MIN = 1
        internal const val ATTEMPTS_MAX = 20
    }

    private val attempts = attempts.coerceIn(ATTEMPTS_MIN, ATTEMPTS_MAX)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var tries = 0
        while (true) {
            tries++
            val finalAttempt = tries >= attempts

            try {
                val response = chain.proceed(chain.request())

                // retry request if the response indicates the session is invalid
                if (sessionInterceptor?.isSessionInvalid(response) == true && !finalAttempt) {
                    continue
                }

                return response
            } catch (e: SessionApiException) {
                if (finalAttempt) throw e
            }
        }
    }
}
