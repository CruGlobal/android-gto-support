package org.ccci.gto.android.common.dagger

import javax.inject.Provider

class FirstNonNullCachingProvider<T>(private var resolver: (() -> T?)?) : Provider<T?> {
    private var component: T? = null

    override fun get() = component
        ?: resolver?.invoke()?.also {
            component = it
            resolver = null
        }
}
