package org.ccci.gto.android.common.recyclerview.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @deprecated Since v2.1.0, this class gains us nothing, use SimpleDataBindingAdapter instead.
 */
@Deprecated
public abstract class BaseDataBindingAdapter<B extends ViewDataBinding, VH extends DataBindingViewHolder<B>>
        extends RecyclerView.Adapter<VH> {}
