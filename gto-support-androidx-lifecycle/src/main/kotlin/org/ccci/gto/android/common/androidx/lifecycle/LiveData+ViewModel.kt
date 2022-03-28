package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.setTagIfAbsent
import com.karumi.weak.weak
import java.io.Closeable
import java.util.concurrent.atomic.AtomicInteger

private val OBSERVER_INDEX = AtomicInteger(0)
fun <O, T : O> LiveData<T>.observe(viewModel: ViewModel, observer: Observer<O>): Observer<O> {
    observeForever(observer)
    viewModel.setTagIfAbsent(
        "LiveDataObserver-${OBSERVER_INDEX.getAndIncrement()}",
        WeakCloseableObserverWrapper(this, observer)
    )
    return observer
}

private class WeakCloseableObserverWrapper<T>(liveData: LiveData<T>, observer: Observer<in T>) : Closeable {
    private val liveData: LiveData<T>? by weak(liveData)
    private val observer: Observer<in T>? by weak(observer)

    override fun close() {
        observer?.let { liveData?.removeObserver(it) }
    }
}
