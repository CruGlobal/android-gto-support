package org.ccci.gto.android.common.dagger.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface AssistedWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}
