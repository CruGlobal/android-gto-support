package org.ccci.gto.android.common.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@MainThread
inline fun <T> LiveData<T>.observeOnce(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) = object : Observer<T> {
    override fun onChanged(t: T) {
        onChanged.invoke(t)
        removeObserver(this)
    }
}.also { observe(owner, it) }
