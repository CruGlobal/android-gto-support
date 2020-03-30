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

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = viewModelProviders[modelClass]
            ?: viewModelProviders.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: return androidViewModelFactory.create(modelClass)

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}
