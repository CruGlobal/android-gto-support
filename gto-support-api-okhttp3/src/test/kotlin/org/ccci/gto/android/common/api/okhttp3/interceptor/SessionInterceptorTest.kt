package org.ccci.gto.android.common.api.okhttp3.interceptor

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.ccci.gto.android.common.api.Session
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

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
            on { loadSession() } doReturn validSession
        }

        sessionInterceptor.intercept(chain)
        verify(sessionInterceptor.loadSession).invoke(any())
        verify(sessionInterceptor.attachSession).invoke(any(), any())
    }

    private class MockSession(id: String?) : Session(id)
    private class MockSessionInterceptor(context: Context) : SessionInterceptor<MockSession>(context) {
        val loadSession = mock<(SharedPreferences) -> MockSession?>()
        val attachSession = mock<(Request, MockSession) -> Request> {
            on { invoke(any(), any()) } doAnswer { it.getArgument(0) }
        }

        fun loadSession() = loadSession(prefs)
        override fun loadSession(prefs: SharedPreferences) = loadSession.invoke(prefs)
        public override fun attachSession(request: Request, session: MockSession) = attachSession.invoke(request, session)
    }
}
