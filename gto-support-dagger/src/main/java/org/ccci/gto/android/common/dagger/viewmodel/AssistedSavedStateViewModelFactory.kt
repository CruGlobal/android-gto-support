package org.ccci.gto.android.common.dagger.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

@Deprecated("Since v3.7.2, use Hilt ViewModel support instead")
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}
