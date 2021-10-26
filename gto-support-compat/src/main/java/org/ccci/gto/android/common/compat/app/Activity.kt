package org.ccci.gto.android.common.compat.app

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import com.karumi.weak.weak
import java.util.WeakHashMap

fun Activity.registerActivityLifecycleCallbacksCompat(callbacks: ActivityLifecycleCallbacks) =
    COMPAT.registerActivityLifecycleCallbacks(this, callbacks)

fun Activity.unregisterActivityLifecycleCallbacksCompat(callbacks: ActivityLifecycleCallbacks) =
    COMPAT.unregisterActivityLifecycleCallbacks(this, callbacks)

private val COMPAT by lazy {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> ActivityCompat.Android10()
        else -> ActivityCompat.Base()
    }
}

private sealed interface ActivityCompat {
    fun registerActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks)
    fun unregisterActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks)

    open class Base : ActivityCompat {
        private val activityLifecycleCallbacksWrappers =
            WeakHashMap<Activity, MutableMap<ActivityLifecycleCallbacks, WrappedCallbacks>>()

        override fun registerActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks) {
            val wrapped = activityLifecycleCallbacksWrappers.getOrPut(activity) { mutableMapOf() }
                .getOrPut(callbacks) { WrappedCallbacks(activity, callbacks) }
            activity.application.registerActivityLifecycleCallbacks(wrapped)
        }

        override fun unregisterActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks) {
            activityLifecycleCallbacksWrappers[activity]?.remove(callbacks)
                ?.let { activity.application.unregisterActivityLifecycleCallbacks(it) }
        }

        private class WrappedCallbacks(activity: Activity, private val delegate: ActivityLifecycleCallbacks) :
            ActivityLifecycleCallbacks {
            private val application = activity.application
            private val activity by weak(activity)

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (this.activity === activity) delegate.onActivityCreated(activity, savedInstanceState)
                removeIfNoLongerValid()
            }

            override fun onActivityStarted(activity: Activity) {
                if (this.activity === activity) delegate.onActivityStarted(activity)
                removeIfNoLongerValid()
            }

            override fun onActivityResumed(activity: Activity) {
                if (this.activity === activity) delegate.onActivityResumed(activity)
                removeIfNoLongerValid()
            }

            override fun onActivityPaused(activity: Activity) {
                if (this.activity === activity) delegate.onActivityPaused(activity)
                removeIfNoLongerValid()
            }

            override fun onActivityStopped(activity: Activity) {
                if (this.activity === activity) delegate.onActivityStopped(activity)
                removeIfNoLongerValid()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                if (this.activity === activity) delegate.onActivitySaveInstanceState(activity, outState)
                removeIfNoLongerValid()
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (this.activity === activity) delegate.onActivityDestroyed(activity)
                removeIfNoLongerValid()
            }

            private fun removeIfNoLongerValid() {
                if (activity == null) application.unregisterActivityLifecycleCallbacks(this)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    class Android10 : Base() {
        override fun registerActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks) {
            activity.registerActivityLifecycleCallbacks(callbacks)
        }

        override fun unregisterActivityLifecycleCallbacks(activity: Activity, callbacks: ActivityLifecycleCallbacks) {
            activity.unregisterActivityLifecycleCallbacks(callbacks)
        }
    }
}
