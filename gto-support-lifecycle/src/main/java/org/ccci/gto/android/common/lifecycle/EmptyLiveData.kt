package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

internal object EmptyLiveData : LiveData<Nothing>() {
    init {
        // TODO: this can be moved to the constructor after we upgrade to Lifecycle 2.1.0
        value = null
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> emptyLiveData(): LiveData<T> = EmptyLiveData as LiveData<T>

inline fun <T> LiveData<T>?.orEmpty() = this ?: emptyLiveData()
