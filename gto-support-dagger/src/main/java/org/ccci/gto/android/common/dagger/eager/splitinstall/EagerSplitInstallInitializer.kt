package org.ccci.gto.android.common.dagger.eager.splitinstall

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import javax.inject.Inject
import javax.inject.Singleton
import org.ccci.gto.android.common.dagger.FirstNonNullCachingProvider
import org.ccci.gto.android.common.dagger.eager.EagerSingletonInitializer

@Singleton
class EagerSplitInstallInitializer @Inject constructor(
    private val baseInitializer: EagerSingletonInitializer,
    private val splitInstallManager: SplitInstallManager,
    splitInstallComponents: Map<String,
        @JvmSuppressWildcards FirstNonNullCachingProvider<EagerSingletonInitializerProvider>>
) : SplitInstallStateUpdatedListener {
    private var components = splitInstallComponents.takeUnless { it.isEmpty() }?.toMutableMap()

    init {
        if (components != null) {
            splitInstallManager.registerListener(this)
            splitInstallManager.installedModules.forEach { initializeSplit(it) }
        }
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        if (state.status() == SplitInstallSessionStatus.INSTALLED) state.moduleNames().forEach { initializeSplit(it) }
    }

    private fun initializeSplit(name: String) {
        if (splitInstallManager.installedModules.contains(name)) {
            components?.get(name)?.get()?.eagerSingletonInitializer()?.let {
                if (baseInitializer.activityCreated) it.initializeActivityCreatedSingletons()
                components?.remove(name)
            }
        }

        if (components.isNullOrEmpty()) {
            splitInstallManager.unregisterListener(this)
            components = null
        }
    }
}
