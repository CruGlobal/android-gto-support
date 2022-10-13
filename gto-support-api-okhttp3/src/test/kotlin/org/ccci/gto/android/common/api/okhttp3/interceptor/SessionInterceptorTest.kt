package org.ccci.gto.android.common.api.okhttp3.interceptor

import android.content.Context
import android.content.SharedPreferences
import io.mockk.Called
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import java.io.IOException
import java.util.concurrent.Callable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.ccci.gto.android.common.api.Session
import org.ccci.gto.android.common.api.okhttp3.EstablishSessionApiException
import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException
import org.junit.Assert.assertThrows
import org.junit.Test

class SessionInterceptorTest {
    private val response: Response = mockk()
    private val chain: Interceptor.Chain = mockk {
        every { request() } returns Request.Builder().url("https://example.com").build()
        every { proceed(any()) } returns response
    }
    private val context: Context = mockk(relaxed = true)
    private val sessionInterceptor = MockSessionInterceptor(context)

    private val validSession = spyk(MockSession("valid")) {
        excludeRecords { isValid }
    }
    private val invalidSession = spyk(MockSession(null))

    @Test
    fun `Existing session is valid`() {
        every { sessionInterceptor.loadSession(any()) } returns validSession

        sessionInterceptor.intercept(chain)
        verifyAll {
            sessionInterceptor.establishSession wasNot Called
            sessionInterceptor.attachSession(any(), any())
        }
        verify(exactly = 0) { validSession.save(any()) }
    }

    @Test
    fun `Existing session is invalid - establishSession() returns valid session`() {
        every { sessionInterceptor.loadSession(any()) } returns invalidSession
        every { sessionInterceptor.establishSession() } returns validSession

        sessionInterceptor.intercept(chain)
        verifyAll {
            sessionInterceptor.establishSession.call()
            sessionInterceptor.attachSession(any(), validSession)
            validSession.save(any())
        }
        verify(exactly = 0) { invalidSession.save(any()) }
    }

    @Test
    fun `Existing session is invalid - establishSession() returns null`() {
        every { sessionInterceptor.loadSession(any()) } returns invalidSession
        every { sessionInterceptor.establishSession() } returns null

        assertThrows(InvalidSessionApiException::class.java) {
            sessionInterceptor.intercept(chain)
        }
        verifyAll {
            sessionInterceptor.establishSession.call()
            sessionInterceptor.attachSession wasNot Called
        }
        verify(exactly = 0) { invalidSession.save(any()) }
    }

    @Test
    fun `Existing session is invalid - establishSession() throws IOException`() {
        every { sessionInterceptor.loadSession(any()) } returns invalidSession
        every { sessionInterceptor.establishSession() } throws IOException()

        assertThrows(EstablishSessionApiException::class.java) {
            sessionInterceptor.intercept(chain)
        }
        verifyAll {
            sessionInterceptor.establishSession.call()
            sessionInterceptor.attachSession wasNot Called
        }
        verify(exactly = 0) { invalidSession.save(any()) }
    }

    @Test
    fun `Existing session is null - establishSession() returns valid session`() {
        every { sessionInterceptor.loadSession(any()) } returns null
        every { sessionInterceptor.establishSession() } returns validSession

        sessionInterceptor.intercept(chain)
        verifyAll {
            sessionInterceptor.establishSession.call()
            sessionInterceptor.attachSession(any(), validSession)
            validSession.save(any())
        }
    }

    private class MockSession(id: String?) : Session(id)
    private class MockSessionInterceptor(context: Context) : SessionInterceptor<MockSession>(context) {
        val loadSession = mockk<(SharedPreferences) -> MockSession?>()
        val establishSession = mockk<Callable<MockSession?>>()
        val attachSession = mockk<(Request, MockSession) -> Request>().also {
            every { it(any(), any()) } answers { it.invocation.args[0] as Request }
        }

        override fun loadSession(prefs: SharedPreferences) = loadSession.invoke(prefs)
        public override fun establishSession() = establishSession.call()
        override fun attachSession(request: Request, session: MockSession) = attachSession.invoke(request, session)
    }
}
