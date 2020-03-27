package org.ccci.gto.android.common.dagger.eager

import dagger.Module
import dagger.multibindings.Multibinds
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode

@Module
abstract class EagerModule {
    @Multibinds
    @EagerSingleton(threadMode = ThreadMode.MAIN)
    abstract fun mainThreadEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(threadMode = ThreadMode.MAIN_ASYNC)
    abstract fun mainAsyncThreadEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(threadMode = ThreadMode.BACKGROUND)
    abstract fun backgroundThreadEagerSingletons(): Set<Any>
}
