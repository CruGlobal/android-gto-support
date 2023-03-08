package org.ccci.gto.android.common.realm.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.karumi.weak.weak
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmRecyclerViewAdapter
import org.ccci.gto.android.common.androidx.recyclerview.adapter.DataBindingViewHolder

abstract class RealmDataBindingAdapter<T : RealmModel, B : ViewDataBinding>(
    lifecycleOwner: LifecycleOwner? = null,
    data: OrderedRealmCollection<T>? = null,
) : RealmRecyclerViewAdapter<T, DataBindingViewHolder<B>>(data, true), Observer<OrderedRealmCollection<T>?> {
    private val lifecycleOwner by weak(lifecycleOwner)

    // region Lifecycle
    override fun onChanged(value: OrderedRealmCollection<T>?) = updateData(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataBindingViewHolder(
        onCreateViewDataBinding(parent, viewType)
            .also { it.lifecycleOwner = lifecycleOwner }
    )

    override fun onBindViewHolder(holder: DataBindingViewHolder<B>, position: Int) {
        onBindViewDataBinding(holder.binding, position)
        holder.binding.executePendingBindings()
    }

    override fun onViewRecycled(holder: DataBindingViewHolder<B>) = onViewDataBindingRecycled(holder.binding)

    protected abstract fun onCreateViewDataBinding(parent: ViewGroup, viewType: Int): B
    protected abstract fun onBindViewDataBinding(binding: B, position: Int)
    protected fun onViewDataBindingRecycled(binding: B) = Unit
    // end Lifecycle

    override fun updateData(data: OrderedRealmCollection<T>?) =
        if (data !== this.data || data?.isManaged == false) super.updateData(data) else Unit
}
