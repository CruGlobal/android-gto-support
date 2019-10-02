package org.ccci.gto.android.common.lifecycle

import androidx.lifecycle.LiveData

private object EmptyLiveData : LiveData<Nothing?>(null)

@Suppress("UNCHECKED_CAST")
fun <T> emptyLiveData(): LiveData<T?> = EmptyLiveData as LiveData<T?>

@Suppress("UNCHECKED_CAST")
@Deprecated("Since 3.1.0, This method can return null for a non-null generic.")
fun <T> LiveData<T>?.orEmpty() = this ?: (EmptyLiveData as LiveData<T>)
