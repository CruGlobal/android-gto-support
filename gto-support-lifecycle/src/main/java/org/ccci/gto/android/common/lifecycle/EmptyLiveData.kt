package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

internal object EmptyLiveData : LiveData<Nothing>() {
    init {
        value = null
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> emptyLiveData(): LiveData<T> = EmptyLiveData as LiveData<T>

inline fun <T> LiveData<T>?.orEmpty() = this ?: emptyLiveData()
