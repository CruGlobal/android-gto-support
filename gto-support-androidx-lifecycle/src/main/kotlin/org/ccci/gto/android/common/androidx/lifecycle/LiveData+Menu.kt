package org.ccci.gto.android.common.androidx.lifecycle

import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, menu: Menu, observer: Menu.(T) -> Unit) =
    observeWeak(lifecycleOwner, menu, observer)

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, item: MenuItem, observer: MenuItem.(T) -> Unit) =
    observeWeak(lifecycleOwner, item, observer)
