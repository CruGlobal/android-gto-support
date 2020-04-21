package org.ccci.gto.android.common.dagger.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

@Reusable
class DaggerWorkerFactory @Inject constructor(
    private val factories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<AssistedWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val clazz = try {
            Class.forName(workerClassName)
        } catch (e: ClassNotFoundException) {
            return null
        }
        return factories[clazz]?.get()?.create(appContext, workerParameters)
    }
}
