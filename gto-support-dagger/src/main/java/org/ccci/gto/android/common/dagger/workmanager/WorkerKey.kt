package org.ccci.gto.android.common.dagger.workmanager

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass

@Deprecated("Since v3.7.2, use AndroidX Hilt WorkManager support instead")
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
