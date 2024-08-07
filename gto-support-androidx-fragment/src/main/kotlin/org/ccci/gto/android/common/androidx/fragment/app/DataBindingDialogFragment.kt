package org.ccci.gto.android.common.androidx.fragment.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AlertDialog as AppCompatAlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class DataBindingDialogFragment<B : ViewDataBinding>(@LayoutRes private val bindingLayoutRes: Int?) :
    BaseDialogFragment() {
    constructor() : this(null)

    // region Lifecycle
    @CallSuper
    override fun onDestroyView() {
        binding?.let { onDestroyBinding(it) }
        super.onDestroyView()
        binding = null
    }
    // endregion Lifecycle

    // region Binding
    private var binding: B? = null

    private fun createBinding() = inflateBinding(LayoutInflater.from(requireDialog().context)).also {
        it.lifecycleOwner = dialogLifecycleOwner

        binding = it
        onBindingCreated(it)
    }

    protected open fun inflateBinding(layoutInflater: LayoutInflater): B = DataBindingUtil.inflate(
        layoutInflater,
        checkNotNull(bindingLayoutRes) { "Either provide a bindingLayoutRes or override inflateBinding" },
        null,
        false
    )

    protected open fun onBindingCreated(binding: B) = Unit
    protected open fun onDestroyBinding(binding: B) = Unit
    // endregion Binding

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        when (dialog) {
            is AppCompatAlertDialog -> dialog.setView(createBinding().root)
            is AlertDialog -> dialog.setView(createBinding().root)
        }
        super.setupDialog(dialog, style)
    }
}
