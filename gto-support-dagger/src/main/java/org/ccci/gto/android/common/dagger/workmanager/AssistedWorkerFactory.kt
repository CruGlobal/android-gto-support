package org.ccci.gto.android.common.dagger.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

@Deprecated("Since v3.7.2, use AndroidX Hilt WorkManager support instead")
interface AssistedWorkerFactory<T : ListenableWorker> {
    fun create(appContext: Context, params: WorkerParameters): T
}
