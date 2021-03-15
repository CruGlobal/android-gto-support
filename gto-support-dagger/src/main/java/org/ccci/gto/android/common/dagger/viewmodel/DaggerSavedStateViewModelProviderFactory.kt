package org.ccci.gto.android.common.dagger.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Reusable
import javax.inject.Inject

@Deprecated("Since v3.7.2, use Hilt ViewModel support instead")
@Reusable
class DaggerSavedStateViewModelProviderFactory @Inject constructor(
    private val app: Application,
    private val daggerViewModelFactory: DaggerViewModelFactory,
    private val savedStateViewModelFactories: Map<Class<out ViewModel>,
        @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>
) {
    fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null): ViewModelProvider.Factory =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            private val savedStateViewModelFactory by lazy { SavedStateViewModelFactory(app, owner, defaultArgs) }

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) =
                savedStateViewModelFactories[modelClass]?.create(handle) as T?
                    ?: daggerViewModelFactory.createOrNull(modelClass)
                    ?: savedStateViewModelFactory.create(modelClass)
        }
}
