package org.ccci.gto.android.common.recyclerview.util

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import org.ccci.gto.android.common.recyclerview.R

private class RecyclerViewLifecycleObserver(private val recyclerView: RecyclerView) : LifecycleObserver {
    var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            if (field === value) return
            field?.lifecycle?.removeObserver(this)
            field = value
            field?.lifecycle?.addObserver(this)
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onLifecycleEvent() {
        if (lifecycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
            resetRecyclerView()
            lifecycleOwner = null
            return
        }
    }

    private fun resetRecyclerView() {
        // clear the adapter since the RecyclerView adds a data observer to the adapter which holds a reference back to the RecyclerView.
        recyclerView.adapter = null
    }
}

@get:MainThread
private val RecyclerView.lifecycleObserver: RecyclerViewLifecycleObserver
    get() = getTag(R.id.rv_lifecycleObserver) as? RecyclerViewLifecycleObserver
        ?: RecyclerViewLifecycleObserver(this).also { setTag(R.id.rv_lifecycleObserver, it) }

@get:MainThread
@set:MainThread
var RecyclerView.lifecycleOwner: LifecycleOwner?
    get() = lifecycleObserver.lifecycleOwner
    set(value) {
        lifecycleObserver.lifecycleOwner = value
    }
