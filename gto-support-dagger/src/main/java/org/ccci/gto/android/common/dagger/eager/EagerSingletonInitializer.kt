package org.ccci.gto.android.common.dagger.eager

import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EagerSingletonInitializer @Inject constructor(
    @EagerSingleton(threadMode = ThreadMode.MAIN) mainEagerSingletons: Lazy<Set<Any>>,
    @EagerSingleton(threadMode = ThreadMode.MAIN_ASYNC) mainAsyncEagerSingletons: Lazy<Set<Any>>,
    @EagerSingleton(threadMode = ThreadMode.BACKGROUND) backgroundEagerSingletons: Lazy<Set<Any>>
) {
    init {
        mainEagerSingletons.get()
        GlobalScope.launch(Dispatchers.Main) {
            mainAsyncEagerSingletons.get()
            withContext(Dispatchers.Default) { backgroundEagerSingletons.get() }
        }
    }
}
