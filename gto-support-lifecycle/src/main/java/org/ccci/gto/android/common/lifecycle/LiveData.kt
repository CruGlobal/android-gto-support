package org.ccci.gto.android.common.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import org.ccci.gto.android.common.androidx.lifecycle.observeOnce

@MainThread
@Deprecated(
    "Since v3.4.0, use version from gto-support-androidx-lifecycle instead",
    ReplaceWith("observeOnce(owner, onChanged)", "org.ccci.gto.android.common.androidx.lifecycle.observeOnce")
)
inline fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, crossinline onChanged: (T) -> Unit) =
    observeOnce(owner, onChanged)

@MainThread
@Deprecated(
    "Since v3.4.0, use version from gto-support-androidx-lifecycle instead",
    ReplaceWith("observeOnce(onChanged)", "org.ccci.gto.android.common.androidx.lifecycle.observeOnce")
)
inline fun <T> LiveData<T>.observeOnce(crossinline onChanged: (T) -> Unit) = observeOnce(onChanged)
