package org.ccci.gto.android.common.recyclerview.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SimpleDataBindingAdapter<B extends ViewDataBinding>
        extends RecyclerView.Adapter<DataBindingViewHolder<B>> {
    // region Lifecycle Events

    @NonNull
    @Override
    public final DataBindingViewHolder<B> onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new DataBindingViewHolder<>(onCreateViewDataBinding(parent, viewType));
    }

    @NonNull
    protected abstract B onCreateViewDataBinding(@NonNull ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(@NonNull final DataBindingViewHolder<B> holder, final int position) {
        onBindViewDataBinding(holder.binding, position);
    }

    protected abstract void onBindViewDataBinding(@NonNull B binding, int position);

    @Override
    public final void onViewRecycled(@NonNull final DataBindingViewHolder<B> holder) {
        onViewDataBindingRecycled(holder.binding);
    }

    protected void onViewDataBindingRecycled(@NonNull final B binding) { }

    // end Lifecycle Events
}
