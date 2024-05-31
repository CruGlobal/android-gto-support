package org.ccci.gto.android.common.dagger.moshi

import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class MoshiModule {
    @Multibinds
    abstract fun adapterFactories(): Set<JsonAdapter.Factory>

    @Multibinds
    @MoshiAdapter
    abstract fun adapters(): Set<Any>
}
