package org.ccci.gto.android.common.androidx.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.setTagIfAbsent
import com.karumi.weak.weak
import java.io.Closeable
import java.util.concurrent.atomic.AtomicInteger

@MainThread
inline fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, crossinline onChanged: (T) -> Unit) =
    observeOnceObserver(onChanged).also { observe(owner, it) }

@MainThread
inline fun <T> LiveData<T>.observeOnce(crossinline onChanged: (T) -> Unit) =
    observeOnceObserver(onChanged).also { observeForever(it) }

@JvmSynthetic
inline fun <T> LiveData<T>.observeOnceObserver(crossinline onChanged: (T) -> Unit) = object : Observer<T> {
    override fun onChanged(t: T) {
        onChanged.invoke(t)
        removeObserver(this)
    }
}

private val OBSERVER_INDEX = AtomicInteger(0)
fun <T> LiveData<T>.observe(viewModel: ViewModel, onChanged: (T) -> Unit): Observer<T> {
    val observer = Observer<T> { t -> onChanged.invoke(t) }
    observeForever(observer)
    viewModel.setTagIfAbsent(
        "LiveDataObserver-${OBSERVER_INDEX.getAndIncrement()}",
        WeakCloseableObserverWrapper(this, observer)
    )
    return observer
}

private class WeakCloseableObserverWrapper<T>(liveData: LiveData<T>, observer: Observer<T>) : Closeable {
    private val liveData: LiveData<T>? by weak(liveData)
    private val observer: Observer<T>? by weak(observer)

    override fun close() {
        observer?.let { liveData?.removeObserver(it) }
    }
}
