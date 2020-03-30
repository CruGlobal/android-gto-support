package org.ccci.gto.android.common.dagger.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

@Reusable
class DaggerViewModelFactory @Inject constructor(
    app: Application,
    private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    private val androidViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        createOrNull(modelClass) ?: androidViewModelFactory.create(modelClass)

    @Suppress("UNCHECKED_CAST")
    internal fun <T : ViewModel> createOrNull(modelClass: Class<T>) = viewModelProviders[modelClass]?.get() as T?
}
