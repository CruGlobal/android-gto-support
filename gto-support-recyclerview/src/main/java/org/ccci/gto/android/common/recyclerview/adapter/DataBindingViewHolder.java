package org.ccci.gto.android.common.recyclerview.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class DataBindingViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
    @NonNull
    public final B binding;

    public DataBindingViewHolder(@NonNull final B binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
