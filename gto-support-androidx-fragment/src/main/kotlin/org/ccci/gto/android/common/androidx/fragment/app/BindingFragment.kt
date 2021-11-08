package org.ccci.gto.android.common.androidx.fragment.app

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BindingFragment<B : ViewBinding> protected constructor(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {
    // region Lifecycle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding(view, savedInstanceState)
    }

    override fun onDestroyView() {
        cleanupDataBinding()
        super.onDestroyView()
    }
    // endregion Lifecycle

    // region View & Data Binding
    private var binding: B? = null

    @Suppress("UNCHECKED_CAST")
    protected open fun resolveBinding(view: View): B? = DataBindingUtil.bind<ViewDataBinding>(view) as? B

    private fun setupDataBinding(view: View, savedInstanceState: Bundle?) {
        binding = resolveBinding(view)?.also {
            if (it is ViewDataBinding) it.lifecycleOwner = viewLifecycleOwner
            onBindingCreated(it, savedInstanceState)
        }
    }

    private fun cleanupDataBinding() {
        binding?.let { onDestroyBinding(it) }
        binding = null
    }

    open fun onBindingCreated(binding: B, savedInstanceState: Bundle?) = Unit
    open fun onDestroyBinding(binding: B) = Unit
    // endregion View & Data Binding
}
