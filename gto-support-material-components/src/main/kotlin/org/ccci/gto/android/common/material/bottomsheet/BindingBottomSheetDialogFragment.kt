package org.ccci.gto.android.common.material.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.ccci.gto.android.common.base.Constants.INVALID_LAYOUT_RES

abstract class BindingBottomSheetDialogFragment<B : ViewBinding>(
    @LayoutRes private val contentLayoutId: Int
) : BottomSheetDialogFragment() {
    constructor() : this(INVALID_LAYOUT_RES)

    private var binding: B? = null

    // region Lifecycle
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = onCreateBinding(inflater, container, savedInstanceState)?.also {
            if (it is ViewDataBinding && it.lifecycleOwner == null) it.lifecycleOwner = viewLifecycleOwner
        }
        return binding?.root ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): B? {
        if (contentLayoutId != INVALID_LAYOUT_RES)
            return DataBindingUtil.inflate<ViewDataBinding>(inflater, contentLayoutId, container, false) as? B
        return null
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
