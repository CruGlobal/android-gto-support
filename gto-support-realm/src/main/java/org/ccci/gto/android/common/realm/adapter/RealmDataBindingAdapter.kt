package org.ccci.gto.android.common.realm.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmRecyclerViewAdapter
import org.ccci.gto.android.common.recyclerview.adapter.DataBindingViewHolder

abstract class RealmDataBindingAdapter<T : RealmModel, B : ViewDataBinding>(data: OrderedRealmCollection<T>? = null) :
    RealmRecyclerViewAdapter<T, DataBindingViewHolder<B>>(data, true), Observer<OrderedRealmCollection<T>?> {
    // region Lifecycle Events

    override fun onChanged(t: OrderedRealmCollection<T>?) {
        updateData(t)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataBindingViewHolder(onCreateViewDataBinding(parent, viewType))

    protected abstract fun onCreateViewDataBinding(parent: ViewGroup, viewType: Int): B

    override fun onBindViewHolder(holder: DataBindingViewHolder<B>, position: Int) =
        onBindViewDataBinding(holder.binding, position)

    protected abstract fun onBindViewDataBinding(binding: B, position: Int)

    override fun onViewRecycled(holder: DataBindingViewHolder<B>) = onViewDataBindingRecycled(holder.binding)

    protected fun onViewDataBindingRecycled(binding: B) {}

    // end Lifecycle Events

    override fun updateData(data: OrderedRealmCollection<T>?) =
        if (data !== this.data || data?.isManaged == false) super.updateData(data) else Unit
}
