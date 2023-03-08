package org.ccci.gto.android.common.androidx.databinding.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

abstract class DataBindingArrayAdapter<B : ViewDataBinding, T>(
    context: Context,
    @LayoutRes private val layout: Int,
    private val lifecycleOwner: LifecycleOwner? = null,
) : ArrayAdapter<T>(context, layout), Observer<Collection<T>> {
    final override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        (convertView?.getBinding() ?: inflateBinding(parent))
            .also { onBind(it, position) }
            .root

    private fun View.getBinding(): B? = DataBindingUtil.getBinding(this)
    private fun inflateBinding(parent: ViewGroup): B =
        DataBindingUtil.inflate<B>(LayoutInflater.from(parent.context), layout, parent, false)
            .also {
                it.lifecycleOwner = lifecycleOwner
                onBindingCreated(it)
            }

    protected abstract fun onBindingCreated(binding: B)
    protected abstract fun onBind(binding: B, position: Int)

    override fun onChanged(value: Collection<T>) {
        clear()
        addAll(value)
    }
}
