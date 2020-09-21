package org.ccci.gto.android.common.dagger.eager

import dagger.Module
import dagger.multibindings.Multibinds
import org.ccci.gto.android.common.dagger.FirstNonNullCachingProvider
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.LifecycleEvent.ACTIVITY_CREATED
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.LifecycleEvent.IMMEDIATE
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.ASYNC
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.MAIN
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.MAIN_ASYNC
import org.ccci.gto.android.common.dagger.eager.splitinstall.EagerSingletonInitializerProvider

@Module
abstract class EagerModule {
    @Multibinds
    @EagerSingleton(on = IMMEDIATE, threadMode = MAIN)
    abstract fun immediateMainEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(on = IMMEDIATE, threadMode = MAIN_ASYNC)
    abstract fun immediateMainAsyncEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(on = IMMEDIATE, threadMode = ASYNC)
    abstract fun immediateAsyncEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = MAIN)
    abstract fun activityMainEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = MAIN_ASYNC)
    abstract fun activityMainAsyncEagerSingletons(): Set<Any>

    @Multibinds
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = ASYNC)
    abstract fun activityAsyncEagerSingletons(): Set<Any>

    @Multibinds
    abstract fun splitInstallComponents(): Map<String, FirstNonNullCachingProvider<EagerSingletonInitializerProvider>>
}
