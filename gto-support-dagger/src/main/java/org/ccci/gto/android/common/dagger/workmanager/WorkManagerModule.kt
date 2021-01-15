package org.ccci.gto.android.common.dagger.workmanager

import androidx.work.ListenableWorker
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class WorkManagerModule {
    @Multibinds
    abstract fun workerFactories(): Map<Class<out ListenableWorker>, AssistedWorkerFactory<out ListenableWorker>>
}
