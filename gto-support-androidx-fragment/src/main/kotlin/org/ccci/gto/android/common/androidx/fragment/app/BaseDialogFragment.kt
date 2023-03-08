package org.ccci.gto.android.common.androidx.fragment.app

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

abstract class BaseDialogFragment : DialogFragment() {
    // region Lifecycle
    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        _dialogLifecycleOwner = FragmentDialogLifecycleOwner()
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        initializeDialogLifecycleOwner()
        return inflater
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        _dialogLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroyView()
        _dialogLifecycleOwner = null
    }
    // endregion Lifecycle

    // region DialogLifecycleOwner
    private var _dialogLifecycleOwner: FragmentDialogLifecycleOwner? = null
    val dialogLifecycleOwner: LifecycleOwner
        get() = _dialogLifecycleOwner
            ?: throw IllegalStateException(
                "Can't access the Fragment Dialog's LifecycleOwner when getDialog() is null " +
                    "i.e., before onCreateDialog() or after onDestroyView()"
            )

    private fun initializeDialogLifecycleOwner() {
        when {
            dialog != null -> _dialogLifecycleOwner!!.initialize()
            _dialogLifecycleOwner!!.isInitialized ->
                throw IllegalStateException("Called getDialogLifecycleOwner() but onCreateDialog() returned null")
            else -> _dialogLifecycleOwner = null
        }
    }
    // endregion DialogLifecycleOwner
}

internal class FragmentDialogLifecycleOwner : LifecycleOwner {
    private var lifecycleRegistry: LifecycleRegistry? = null

    internal fun initialize() {
        if (lifecycleRegistry == null) {
            lifecycleRegistry = LifecycleRegistry(this)
        }
    }

    internal val isInitialized = lifecycleRegistry != null

    override val lifecycle: Lifecycle
        get() {
            initialize()
            return lifecycleRegistry!!
        }

    fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry!!.handleLifecycleEvent(event)
}
