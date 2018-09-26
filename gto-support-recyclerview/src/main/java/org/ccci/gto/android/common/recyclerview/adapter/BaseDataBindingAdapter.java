package org.ccci.gto.android.common.recyclerview.adapter;

import org.ccci.gto.android.common.recyclerview.adapter.BaseDataBindingAdapter.DataBindingViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseDataBindingAdapter<B extends ViewDataBinding, VH extends DataBindingViewHolder<B>>
        extends RecyclerView.Adapter<VH> {
    public static class DataBindingViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        @NonNull
        public final B binding;

        public DataBindingViewHolder(@NonNull final B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
