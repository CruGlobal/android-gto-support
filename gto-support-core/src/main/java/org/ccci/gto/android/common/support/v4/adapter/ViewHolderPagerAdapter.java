package org.ccci.gto.android.common.support.v4.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

public abstract class ViewHolderPagerAdapter<VH extends ViewHolderPagerAdapter.ViewHolder> extends PagerAdapter {
    public static final long NO_ID = -1;

    private final SparseArray<VH> mActive = new SparseArray<>();
    private final LinkedList<VH> mRecycled = new LinkedList<>();

    private boolean mHasStableIds = false;

    public ViewHolderPagerAdapter() {
    }

    public void setHasStableIds(final boolean hasStableIds) {
        mHasStableIds = hasStableIds;
    }

    protected boolean hasStableIds() {
        return mHasStableIds;
    }

    public long getItemId(final int position) {
        return NO_ID;
    }

    protected int getItemPositionFromId(final long id) {
        return POSITION_NONE;
    }

    @Override
    public int getItemPosition(@NonNull final Object holder) {
        if (hasStableIds()) {
            return getItemPositionFromId(((ViewHolder) holder).mId);
        }

        return POSITION_NONE;
    }

    @Override
    public final boolean isViewFromObject(@NonNull final View view, @NonNull final Object holder) {
        return view == ((ViewHolder) holder).mView;
    }

    @Override
    public final VH instantiateItem(@NonNull final ViewGroup container, final int position) {
        // recycle or create ViewHolder
        VH holder = mRecycled.poll();
        if (holder == null) {
            holder = onCreateViewHolder(container);
        }
        mActive.put(holder.hashCode(), holder);

        // add view to container
        container.addView(holder.mView);

        // bind ViewHolder
        onBindViewHolder(holder, position);

        // return this ViewHolder
        return holder;
    }

    @Override
    public void notifyDataSetChanged() {
        // process all active ViewHolders
        for (int i = 0; i < mActive.size(); i++) {
            final VH holder = mActive.valueAt(i);

            // get the update position
            int pos = getItemPosition(holder);
            if (pos == POSITION_NONE) {
                continue;
            } else if (pos == POSITION_UNCHANGED) {
                pos = holder.mLastKnownPosition;
            }

            // rebind data with the updated position
            onBindViewHolder(holder, pos);
        }

        // trigger other updates
        super.notifyDataSetChanged();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void destroyItem(@NonNull final ViewGroup container, final int position,
                                  @NonNull final Object object) {
        final VH holder = (VH) object;

        // remove the view from the container
        container.removeView(holder.mView);

        // recycle this ViewHolder
        mActive.delete(object.hashCode());
        onViewRecycled(holder);
        mRecycled.add(holder);
    }

    @NonNull
    protected abstract VH onCreateViewHolder(@NonNull ViewGroup parent);

    protected void onBindViewHolder(@NonNull final VH holder, final int position) {
        holder.mId = getItemId(position);
        holder.mLastKnownPosition = position;
    }

    protected void onViewRecycled(@NonNull final VH holder) {
    }

    protected static class ViewHolder {
        long mId = NO_ID;
        int mLastKnownPosition = POSITION_NONE;

        @NonNull
        final View mView;

        protected ViewHolder(@NonNull final View view) {
            mView = view;
        }

        protected long getId() {
            return mId;
        }

        protected int getLastKnownPosition() {
            return mLastKnownPosition;
        }
    }
}
