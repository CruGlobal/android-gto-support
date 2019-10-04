package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

private object EmptyLiveData : LiveData<Nothing?>(null)

@Suppress("UNCHECKED_CAST")
fun <T> emptyLiveData(): LiveData<T?> = EmptyLiveData as LiveData<T?>

fun <T> LiveData<T>?.orEmpty(): LiveData<out T?> = this ?: emptyLiveData()
