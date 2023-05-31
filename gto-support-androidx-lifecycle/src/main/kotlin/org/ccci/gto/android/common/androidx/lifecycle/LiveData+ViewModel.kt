package org.ccci.gto.android.common.androidx.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.karumi.weak.weak
import java.io.Closeable

fun <O, T : O> LiveData<T>.observe(viewModel: ViewModel, observer: Observer<O>): Observer<O> {
    observeForever(observer)
    viewModel.addCloseable(WeakCloseableObserverWrapper(this, observer))
    return observer
}

private class WeakCloseableObserverWrapper<T>(liveData: LiveData<T>, observer: Observer<in T>) : Closeable {
    private val liveData: LiveData<T>? by weak(liveData)
    private val observer: Observer<in T>? by weak(observer)

    override fun close() {
        observer?.let { liveData?.removeObserver(it) }
    }
}

// region Multi-observe
fun <IN1, IN2> ViewModel.observe(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    observer: (IN1, IN2) -> Unit
) = compoundLiveData(source1, source2).observe(this) {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2)
}

fun <IN1, IN2, IN3> ViewModel.observe(
    source1: LiveData<IN1>,
    source2: LiveData<IN2>,
    source3: LiveData<IN3>,
    observer: (IN1, IN2, IN3) -> Unit
) = compoundLiveData(source1, source2, source3).observe(this) {
    @Suppress("UNCHECKED_CAST")
    observer(source1.value as IN1, source2.value as IN2, source3.value as IN3)
}
// endregion Multi-observe

@MainThread
inline fun <T> LiveData<T>.observeOnce(viewModel: ViewModel, crossinline onChanged: (T) -> Unit) =
    observeOnceObserver(onChanged).also { observe(viewModel, it) }
