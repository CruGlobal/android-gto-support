package org.ccci.gto.android.common.androidx.fragment.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BindingFragment<B : ViewBinding> protected constructor(@LayoutRes private val contentLayoutId: Int) :
    Fragment(contentLayoutId) {
    protected constructor() : this(0)

    private var binding: B? = null

    // region Lifecycle
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = onCreateBinding(inflater, container, savedInstanceState)
            ?.also { if (it is ViewDataBinding && it.lifecycleOwner == null) it.lifecycleOwner = viewLifecycleOwner }
        return binding?.root ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    protected open fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): B? = when {
        contentLayoutId != 0 ->
            @Suppress("UNCHECKED_CAST")
            DataBindingUtil.inflate<ViewDataBinding>(inflater, contentLayoutId, container, false) as? B

        else -> null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { onBindingCreated(it, savedInstanceState) }
    }

    open fun onBindingCreated(binding: B, savedInstanceState: Bundle?) = Unit

    override fun onDestroyView() {
        binding?.let { onDestroyBinding(it) }
        binding = null
        super.onDestroyView()
    }

    open fun onDestroyBinding(binding: B) = Unit
    // endregion Lifecycle
}
