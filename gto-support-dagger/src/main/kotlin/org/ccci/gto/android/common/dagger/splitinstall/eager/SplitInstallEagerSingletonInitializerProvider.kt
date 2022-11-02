package org.ccci.gto.android.common.dagger.splitinstall.eager

import org.ccci.gto.android.common.dagger.eager.EagerSingletonInitializer
import org.ccci.gto.android.common.dagger.splitinstall.SplitInstallComponent

interface SplitInstallEagerSingletonInitializerProvider : SplitInstallComponent {
    fun eagerSingletonInitializer(): EagerSingletonInitializer
}
