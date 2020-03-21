package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData

private object EmptyLiveData : LiveData<Nothing?>(null)

@Suppress("UNCHECKED_CAST")
fun <T> emptyLiveData(): LiveData<T?> = EmptyLiveData as LiveData<T?>

@Suppress("UNCHECKED_CAST")
fun <T> LiveData<T>?.orEmpty(): LiveData<T?> = this as? LiveData<T?> ?: emptyLiveData()
