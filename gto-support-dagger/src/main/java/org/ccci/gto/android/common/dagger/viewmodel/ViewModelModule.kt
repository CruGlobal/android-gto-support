package org.ccci.gto.android.common.dagger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class ViewModelModule {
    @Multibinds
    abstract fun viewModels(): Map<Class<out ViewModel>, ViewModel>

    @Binds
    internal abstract fun bindDaggerViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}
