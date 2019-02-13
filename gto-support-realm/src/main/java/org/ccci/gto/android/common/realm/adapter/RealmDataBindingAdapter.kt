package org.ccci.gto.android.common.realm.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import io.realm.RealmRecyclerViewAdapter
import org.ccci.gto.android.common.recyclerview.adapter.DataBindingViewHolder

abstract class RealmDataBindingAdapter<T : RealmModel, B : ViewDataBinding>(data: OrderedRealmCollection<T>? = null) :
    RealmRecyclerViewAdapter<T, DataBindingViewHolder<B>>(data, true) {
    // region Lifecycle Events

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataBindingViewHolder(onCreateViewDataBinding(parent, viewType))

    protected abstract fun onCreateViewDataBinding(parent: ViewGroup, viewType: Int): B

    override fun onBindViewHolder(holder: DataBindingViewHolder<B>, position: Int) =
        onBindViewDataBinding(holder.binding, position)

    protected abstract fun onBindViewDataBinding(binding: B, position: Int)

    override fun onViewRecycled(holder: DataBindingViewHolder<B>) = onViewDataBindingRecycled(holder.binding)

    protected fun onViewDataBindingRecycled(binding: B) {}

    // end Lifecycle Events
}
