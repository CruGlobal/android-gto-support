package org.ccci.gto.android.common.dagger.eager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.LifecycleEvent.ACTIVITY_CREATED
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.LifecycleEvent.IMMEDIATE
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.ASYNC
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.MAIN
import org.ccci.gto.android.common.dagger.eager.EagerSingleton.ThreadMode.MAIN_ASYNC

@Singleton
class EagerSingletonInitializer @Inject constructor(
    app: Application,
    @EagerSingleton(on = IMMEDIATE, threadMode = MAIN) immediateMain: Lazy<Set<Any>>,
    @EagerSingleton(on = IMMEDIATE, threadMode = MAIN_ASYNC) immediateMainAsync: Lazy<Set<Any>>,
    @EagerSingleton(on = IMMEDIATE, threadMode = ASYNC) immediateAsync: Lazy<Set<Any>>,
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = MAIN) activityMain: Lazy<Set<Any>>,
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = MAIN_ASYNC) activityMainAsync: Lazy<Set<Any>>,
    @EagerSingleton(on = ACTIVITY_CREATED, threadMode = ASYNC) activityAsync: Lazy<Set<Any>>,
) : Application.ActivityLifecycleCallbacks {
    @VisibleForTesting
    internal val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    private var app: Application? = app
    private var activityMain: Lazy<Set<Any>>? = activityMain
    private var activityMainAsync: Lazy<Set<Any>>? = activityMainAsync
    private var activityAsync: Lazy<Set<Any>>? = activityAsync
    internal var activityCreated = false

    init {
        initialize(immediateMain, immediateMainAsync, immediateAsync)
        app.registerActivityLifecycleCallbacks(this)
    }

    // region Application.ActivityLifecycleCallbacks
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) =
        initializeActivityCreatedSingletons()
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
    // endregion Application.ActivityLifecycleCallbacks

    private fun initialize(main: Lazy<Set<Any>>?, mainAsync: Lazy<Set<Any>>?, async: Lazy<Set<Any>>?) {
        main?.get()
        coroutineScope.launch(Dispatchers.Main) {
            mainAsync?.get()
            withContext(Dispatchers.Default) { async?.get() }
        }
    }

    internal fun initializeActivityCreatedSingletons() {
        activityCreated = true
        app?.unregisterActivityLifecycleCallbacks(this)
        initialize(activityMain, activityMainAsync, activityAsync)
        activityMain = null
        activityMainAsync = null
        activityAsync = null
        app = null
    }
}
