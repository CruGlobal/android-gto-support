package org.ccci.gto.android.common.api.okhttp3.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import okhttp3.Interceptor
import okhttp3.Response
import org.ccci.gto.android.common.api.okhttp3.SessionApiException
import org.ccci.gto.android.common.api.okhttp3.interceptor.SessionRetryInterceptor.ATTEMPTS_MAX
import org.ccci.gto.android.common.api.okhttp3.interceptor.SessionRetryInterceptor.ATTEMPTS_MIN

class SessionRetryInterceptorTest {
    private val sessionInterceptor: SessionInterceptor<*> = mockk {
        every { isSessionInvalid(any()) } returns false
    }
    private val chain: Interceptor.Chain = mockk {
        every { request() } returns mockk()
        every { proceed(any()) } throws SessionApiException()
    }

    @Test
    fun `Property attempts - at least ATTEMPTS_MIN attempts`() {
        val interceptor = SessionRetryInterceptor(ATTEMPTS_MIN - 1)

        assertFailsWith<SessionApiException> { interceptor.intercept(chain) }
        verify(exactly = ATTEMPTS_MIN) { chain.proceed(any()) }
    }

    @Test
    fun `Property attempts - at most ATTEMPTS_MAX attempts`() {
        val interceptor = SessionRetryInterceptor(ATTEMPTS_MAX + 1)

        assertFailsWith<SessionApiException> { interceptor.intercept(chain) }
        verify(exactly = ATTEMPTS_MAX) { chain.proceed(any()) }
    }

    @Test
    fun `intercept() - Response - Valid`() {
        val interceptor = SessionRetryInterceptor()
        val response: Response = mockk()
        every { chain.proceed(any()) } returns response

        assertEquals(response, interceptor.intercept(chain))
        verify(exactly = 1) { chain.proceed(any()) }
    }

    @Test
    fun `intercept() - Response - Valid - After session exception`() {
        val interceptor = SessionRetryInterceptor()
        val response: Response = mockk()
        every { chain.proceed(any()) } throws SessionApiException() andThen response

        assertEquals(response, interceptor.intercept(chain))
        verify(exactly = 2) { chain.proceed(any()) }
    }

    @Test
    fun `intercept() - Response - Session expired followed by valid`() {
        val interceptor = SessionRetryInterceptor(sessionInterceptor, 10)
        val expired: Response = mockk()
        val response: Response = mockk()
        every { chain.proceed(any()) } returns expired andThen response
        every { sessionInterceptor.isSessionInvalid(expired) } returns true

        assertEquals(response, interceptor.intercept(chain))
        verify(exactly = 2) { chain.proceed(any()) }
    }

    @Test
    fun `intercept() - Exception - Propagate`() {
        val interceptor = SessionRetryInterceptor()
        every { chain.proceed(any()) } throws IOException()

        assertFailsWith<IOException> { interceptor.intercept(chain) }
        verify(exactly = 1) { chain.proceed(any()) }
    }
}
