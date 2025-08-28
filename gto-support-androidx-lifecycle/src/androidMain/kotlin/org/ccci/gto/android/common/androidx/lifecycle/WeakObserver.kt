package org.ccci.gto.android.common.androidx.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.karumi.weak.WeakReferenceDelegate
import com.karumi.weak.weak

internal fun <O : Any, T> LiveData<T>.observeWeak(
    lifecycleOwner: LifecycleOwner,
    obj: O,
    observer: O.(T) -> Unit
): Observer<T> = WeakObserver(this, obj, observer).also { observe(lifecycleOwner, it) }

private class WeakObserver<O : Any, T>(liveData: LiveData<T>, obj: O, private val observer: O.(T) -> Unit) :
    Observer<T> {
    private val liveData by weak(liveData)
    private val obj by WeakReferenceDelegate(obj)

    override fun onChanged(value: T) {
        when (val obj = obj) {
            null -> liveData?.removeObserver(this)
            else -> obj.observer(value)
        }
    }
}
