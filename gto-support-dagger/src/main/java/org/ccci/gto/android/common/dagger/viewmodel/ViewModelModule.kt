package org.ccci.gto.android.common.dagger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds

@Deprecated("Since v3.7.2, use Hilt ViewModel support instead")
@Module
abstract class ViewModelModule {
    @Multibinds
    abstract fun viewModels(): Map<Class<out ViewModel>, ViewModel>

    @Multibinds
    abstract fun assistedSavedStateViewModelFactories():
        Map<Class<out ViewModel>, AssistedSavedStateViewModelFactory<ViewModel>>

    @Binds
    internal abstract fun bindDaggerViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}
