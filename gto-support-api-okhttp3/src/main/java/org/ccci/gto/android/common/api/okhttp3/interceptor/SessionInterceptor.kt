package org.ccci.gto.android.common.api.okhttp3.interceptor

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CallSuper
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import org.ccci.gto.android.common.api.Session
import org.ccci.gto.android.common.api.okhttp3.EstablishSessionApiException
import org.ccci.gto.android.common.api.okhttp3.InvalidSessionApiException

abstract class SessionInterceptor<S : Session?> protected constructor(
    context: Context, returnInvalidSessionResponses: Boolean = DEFAULT_RETURN_INVALID_SESSION_RESPONSES,
    prefFile: String? = null
) : Interceptor {
    protected val mLockSession = Any()
    protected val mContext: Context
    private val mReturnInvalidSessionResponses: Boolean
    private val mPrefFile: String

    protected constructor(context: Context, prefFile: String?) : this(
        context,
        DEFAULT_RETURN_INVALID_SESSION_RESPONSES,
        prefFile
    ) {
    }

    protected val prefs: SharedPreferences
        protected get() = mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE)

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        // get the session, establish a session if one doesn't exist or if we have a stale session
        var session: S?
        synchronized(mLockSession) {
            session = loadSession()
            if (session == null) {
                session = try {
                    establishSession()
                } catch (e: IOException) {
                    // wrap establish session IOExceptions
                    throw EstablishSessionApiException(e)
                }

                // save the newly established session
                if (session != null && session!!.isValid) {
                    saveSession(session!!)
                }
            }
        }

        // throw an exception if we don't have a valid session
        if (session == null) {
            throw InvalidSessionApiException()
        }

        // process request & response
        return processResponse(chain.proceed(attachSession(chain.request(), session!!)), session!!)
    }

    protected abstract fun attachSession(request: Request, session: S): Request

    @CallSuper
    @Throws(IOException::class)
    protected fun processResponse(response: Response, session: S): Response {
        // make sure the response is valid
        if (isSessionInvalid(response)) {
            // reset the session
            synchronized(mLockSession) {

                // only reset if this is still the same session
                val active = loadSession()
                if (active != null && active.equals(session)) {
                    deleteSession(session)
                }
            }

            // throw an invalid session exception because our session was invalid
            if (!mReturnInvalidSessionResponses) {
                throw InvalidSessionApiException()
            }
        }
        return response
    }

    @Throws(IOException::class)
    fun isSessionInvalid(response: Response): Boolean {
        return false
    }

    private fun loadSession(): S? {
        // load a pre-existing session
        val prefs = prefs
        val session: S?
        synchronized(mLockSession) { session = loadSession(prefs) }

        // only return valid sessions
        return if (session != null && session.isValid) session else null
    }

    protected abstract fun loadSession(prefs: SharedPreferences): S?

    @Throws(IOException::class)
    protected open fun establishSession(): S? {
        return null
    }

    protected fun saveSession(session: S) {
        val prefs = prefs.edit()
        session!!.save(prefs)
        synchronized(mLockSession) { prefs.apply() }
    }

    private fun deleteSession(session: S) {
        val prefs = prefs.edit()
        session!!.delete(prefs)
        synchronized(mLockSession) { prefs.apply() }
    }

    companion object {
        private const val DEFAULT_RETURN_INVALID_SESSION_RESPONSES = false
    }

    init {
        mContext = context.applicationContext
        mReturnInvalidSessionResponses = returnInvalidSessionResponses
        mPrefFile = prefFile ?: javaClass.simpleName
    }
}
