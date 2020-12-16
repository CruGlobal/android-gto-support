package org.ccci.gto.android.common.testing.timber

import timber.log.Timber

object ExceptionRaisingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) throw t
    }

    fun plant() = Timber.plant(this)
    fun uproot() = Timber.uproot(this)
}
