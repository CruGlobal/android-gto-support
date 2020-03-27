package org.ccci.gto.android.common.dagger.eager

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EagerSingleton(val threadMode: ThreadMode = ThreadMode.MAIN_ASYNC) {
    enum class ThreadMode { MAIN, MAIN_ASYNC, BACKGROUND }
}
