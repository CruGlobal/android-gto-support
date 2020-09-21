package org.ccci.gto.android.common.dagger.eager.splitinstall

import org.ccci.gto.android.common.dagger.eager.EagerSingletonInitializer

interface EagerSingletonInitializerProvider {
    fun eagerSingletonInitializer(): EagerSingletonInitializer
}
