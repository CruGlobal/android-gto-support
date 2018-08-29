package org.ccci.gto.android.common.recyclerview.adapter;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import org.ccci.gto.android.common.recyclerview.adapter.BaseDataBindingAdapter.DataBindingViewHolder;

public abstract class SimpleDataBindingAdapter<B extends ViewDataBinding>
        extends BaseDataBindingAdapter<B, DataBindingViewHolder<B>> {
    // region Lifecycle Events

    @NonNull
    @Override
    public final DataBindingViewHolder<B> onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new DataBindingViewHolder<>(onCreateViewDataBinding(parent, viewType));
    }

    @NonNull
    protected abstract B onCreateViewDataBinding(@NonNull final ViewGroup parent, final int viewType);

    // end Lifecycle Events
}
