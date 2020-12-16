package org.ccci.gto.android.common.testing.timber

import timber.log.Timber

object ExceptionRaisingTree : Timber.Tree() {
    private var seen: Throwable? = null

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        seen = seen ?: t
        if (t != null) throw t
    }

    fun plant() = Timber.plant(this)
    fun uproot() {
        Timber.uproot(this)
        seen?.let {
            seen = null
            throw it
        }
    }
}
