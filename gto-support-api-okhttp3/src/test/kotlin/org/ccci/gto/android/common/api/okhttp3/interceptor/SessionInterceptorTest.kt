package org.ccci.gto.android.common.api.okhttp3.interceptor

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.ccci.gto.android.common.api.Session
import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class SessionInterceptorTest {
    private lateinit var response: Response
    private lateinit var chain: Interceptor.Chain
    private lateinit var context: Context
    private lateinit var sessionInterceptor: MockSessionInterceptor

    private val validSession = MockSession("valid")
    private val invalidSession = MockSession(null)

    @Before
    fun setup() {
        response = mock()
        chain = mock {
            on { request() } doReturn Request.Builder().url("https://example.com").build()
            on { proceed(any()) } doReturn response
        }
        context = mock(defaultAnswer = RETURNS_DEEP_STUBS)
        sessionInterceptor = MockSessionInterceptor(context)
    }

    @Test
    fun `Existing session is valid`() {
        sessionInterceptor.stub {
            on { loadSession(any()) } doReturn validSession
        }

        sessionInterceptor.intercept(chain)
        verify(sessionInterceptor.establishSession, never()).invoke()
        verify(sessionInterceptor.attachSession).invoke(any(), any())
    }

    @Test
    fun `Existing session is invalid - establishSession() returns valid session`() {
        sessionInterceptor.stub {
            on { loadSession(any()) } doReturn invalidSession
            on { establishSession() } doReturn validSession
        }

        sessionInterceptor.intercept(chain)
        verify(sessionInterceptor.establishSession).invoke()
        verify(sessionInterceptor.attachSession).invoke(any(), eq(validSession))
        verifyNoMoreInteractions(sessionInterceptor.attachSession)
    }

    @Test
    fun `Existing session is invalid - establishSession() returns null`() {
        sessionInterceptor.stub {
            on { loadSession(any()) } doReturn invalidSession
            on { establishSession() } doReturn null
        }

        assertThrows(InvalidSessionApiException::class.java) {
            sessionInterceptor.intercept(chain)
        }
        verify(sessionInterceptor.establishSession).invoke()
        verify(sessionInterceptor.attachSession, never()).invoke(any(), any())
    }

    @Test
    fun `Existing session is null - establishSession() returns valid session`() {
        sessionInterceptor.stub {
            on { loadSession(any()) } doReturn null
            on { establishSession() } doReturn validSession
        }

        sessionInterceptor.intercept(chain)
        verify(sessionInterceptor.establishSession).invoke()
        verify(sessionInterceptor.attachSession).invoke(any(), eq(validSession))
        verifyNoMoreInteractions(sessionInterceptor.attachSession)
    }

    private class MockSession(id: String?) : Session(id)
    private class MockSessionInterceptor(context: Context) : SessionInterceptor<MockSession>(context) {
        val loadSession = mock<(SharedPreferences) -> MockSession?>()
        val establishSession = mock<() -> MockSession?>()
        val attachSession = mock<(Request, MockSession) -> Request> {
            on { invoke(any(), any()) } doAnswer { it.getArgument(0) }
        }

        override fun loadSession(prefs: SharedPreferences) = loadSession.invoke(prefs)
        override fun establishSession() = establishSession.invoke()
        override fun attachSession(request: Request, session: MockSession) = attachSession.invoke(request, session)
    }
}
