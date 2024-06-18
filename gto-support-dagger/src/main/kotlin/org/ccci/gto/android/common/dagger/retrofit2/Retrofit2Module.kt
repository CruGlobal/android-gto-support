package org.ccci.gto.android.common.dagger.retrofit2

import dagger.Module
import dagger.multibindings.Multibinds
import retrofit2.Converter

@Module
abstract class Retrofit2Module {
    @Multibinds
    abstract fun converterFactories(): Set<Converter.Factory>
}
