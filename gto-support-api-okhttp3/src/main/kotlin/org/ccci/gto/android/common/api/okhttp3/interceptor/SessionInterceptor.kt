package org.ccci.gto.android.common.api.okhttp3.interceptor

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import org.ccci.gto.android.common.api.Session
import org.ccci.gto.android.common.api.okhttp3.EstablishSessionApiException
import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException

private const val DEFAULT_RETURN_INVALID_SESSION_RESPONSES = false

abstract class SessionInterceptor<S : Session> @JvmOverloads protected constructor(
    context: Context,
    private val returnInvalidSessionResponses: Boolean = DEFAULT_RETURN_INVALID_SESSION_RESPONSES,
    prefFile: String? = null
) : Interceptor {
    protected val context: Context = context.applicationContext

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val prefFileName = prefFile?.also { require(it.isNotEmpty()) { "prefFile cannot be an empty string" } }
        ?: generateSequence<Class<*>>(javaClass) { it.superclass }
            .mapNotNull { it.simpleName.takeUnless { it.isEmpty() } }
            .first()
    protected val prefs by lazy { this.context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE) }

    protected val lockSession = Any()

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val session = synchronized(lockSession) {
            // load a valid session
            loadSession()?.takeIf { it.isValid }
                // otherwise try establishing a session
                ?: try {
                    establishSession()?.takeIf { it.isValid }?.also { saveSession(it) }
                } catch (e: IOException) {
                    throw EstablishSessionApiException(e)
                }
        } ?: throw InvalidSessionApiException()

        return processResponse(chain.proceed(attachSession(chain.request(), session)), session)
    }

    protected abstract fun attachSession(request: Request, session: S): Request

    @CallSuper
    @Throws(IOException::class)
    protected open fun processResponse(response: Response, session: S): Response {
        if (isSessionInvalid(response)) {
            // reset the session if this is still the same session
            synchronized(lockSession) {
                loadSession()?.takeIf { it == session }?.apply { deleteSession(this) }
            }

            if (!returnInvalidSessionResponses) throw InvalidSessionApiException()
        }
        return response
    }

    @Throws(IOException::class)
    open fun isSessionInvalid(response: Response) = false

    private fun loadSession() = synchronized(lockSession) { loadSession(prefs) }?.takeIf { it.isValid }
    protected abstract fun loadSession(prefs: SharedPreferences): S?

    @Throws(IOException::class)
    protected open fun establishSession(): S? = null

    private fun saveSession(session: Session) {
        val changes = prefs.edit().apply { session.save(this) }
        synchronized(lockSession) { changes.apply() }
    }

    private fun deleteSession(session: Session) {
        val changes = prefs.edit().apply { session.delete(this) }
        synchronized(lockSession) { changes.apply() }
    }
}
