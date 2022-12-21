package org.ccci.gto.android.common.dagger.eager

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EagerSingleton(
    val on: LifecycleEvent = LifecycleEvent.IMMEDIATE,
    val threadMode: ThreadMode = ThreadMode.ASYNC,
) {
    enum class LifecycleEvent { IMMEDIATE, ACTIVITY_CREATED }
    enum class ThreadMode { MAIN, MAIN_ASYNC, ASYNC }
}
