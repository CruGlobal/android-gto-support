package org.ccci.gto.android.common.viewpager.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.ccci.gto.android.common.util.ParcelableLongSparseArray;
import org.ccci.gto.android.common.util.ParcelableSparseArray;

import java.util.LinkedList;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.customview.view.AbsSavedState;
import androidx.viewpager.widget.PagerAdapter;

public abstract class ViewHolderPagerAdapter<VH extends ViewHolderPagerAdapter.ViewHolder> extends PagerAdapter {
    public static final long NO_ID = -1;

    private final SparseArray<VH> mActive = new SparseArray<>();
    private final LinkedList<VH> mRecycled = new LinkedList<>();
    @Nullable
    private VH mCurrent;

    private boolean mHasStableIds = false;

    @NonNull
    private ParcelableLongSparseArray<Parcelable> mViewHolderSavedState = new ParcelableLongSparseArray<>();

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

    @NonNull
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

        // restore state
        if (hasStableIds()) {
            final long id = getItemId(position);
            if (id != NO_ID) {
                holder.restoreState(mViewHolderSavedState.get(id));
            }
        }

        // return this ViewHolder
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void setPrimaryItem(@NonNull final ViewGroup container, final int position,
                                     @Nullable final Object holder) {
        final VH old = mCurrent;
        mCurrent = (VH) holder;
        if (old != mCurrent) {
            onUpdatePrimaryItem(old, mCurrent);
        }
    }

    @Nullable
    public final VH getPrimaryItem() {
        return mCurrent;
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

    @UiThread
    @Override
    @SuppressWarnings("unchecked")
    public final void destroyItem(@NonNull final ViewGroup container, final int position,
                                  @NonNull final Object object) {
        final VH holder = (VH) object;

        // remove the view from the container
        container.removeView(holder.mView);

        // save the view holder state
        if (hasStableIds()) {
            mViewHolderSavedState.put(holder.mId, holder.mId != NO_ID ? holder.saveState() : null);
        }

        // recycle this ViewHolder
        mActive.delete(object.hashCode());
        onViewHolderRecycled(holder);
        mRecycled.add(holder);
    }

    @Override
    public Parcelable saveState() {
        // update saved state for all active views
        if (hasStableIds()) {
            for (int i = 0; i < mActive.size(); i++) {
                final VH holder = mActive.valueAt(i);
                if (holder.mId != NO_ID) {
                    mViewHolderSavedState.put(holder.mId, holder.saveState());
                }
            }
        }

        // generate the saved state
        final SavedState state = new SavedState(super.saveState());
        state.viewHolderSavedState = mViewHolderSavedState;
        return state;
    }

    @Override
    public void restoreState(@Nullable final Parcelable state, final ClassLoader loader) {
        if (!(state instanceof SavedState)) {
            super.restoreState(state, loader);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.restoreState(ss.getSuperState(), loader);
        mViewHolderSavedState =
                ss.viewHolderSavedState != null ? ss.viewHolderSavedState : new ParcelableLongSparseArray<>();

        // restore state to all active views
        if (hasStableIds()) {
            for (int i = 0; i < mActive.size(); i++) {
                final VH holder = mActive.valueAt(i);
                if (holder.mId != NO_ID) {
                    holder.restoreState(mViewHolderSavedState.get(holder.mId));
                }
            }
        }
    }

    /* BEGIN lifecycle */

    @NonNull
    @UiThread
    protected abstract VH onCreateViewHolder(@NonNull ViewGroup parent);

    @UiThread
    @CallSuper
    protected void onBindViewHolder(@NonNull final VH holder, final int position) {
        holder.mId = getItemId(position);
        holder.mLastKnownPosition = position;
    }

    @UiThread
    protected void onUpdatePrimaryItem(@Nullable final VH old, @Nullable final VH current) {}

    @UiThread
    @CallSuper
    protected void onViewHolderRecycled(@NonNull final VH holder) {
        holder.mId = NO_ID;
        holder.mLastKnownPosition = POSITION_NONE;
    }

    /* END lifecycle */

    public static class ViewHolder {
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

        protected Parcelable saveState() {
            final ParcelableSparseArray<Parcelable> state = new ParcelableSparseArray<>();
            mView.saveHierarchyState(state);
            return state;
        }

        protected void restoreState(@Nullable final Parcelable state) {
            if (state instanceof ParcelableSparseArray) {
                //noinspection unchecked
                mView.restoreHierarchyState((ParcelableSparseArray) state);
            }
        }
    }

    static class SavedState extends AbsSavedState {
        @Nullable
        ParcelableLongSparseArray<Parcelable> viewHolderSavedState;

        SavedState(@Nullable final Parcelable superState) {
            super(superState != null ? superState : EMPTY_STATE);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(viewHolderSavedState, 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(final Parcel source) {
                return createFromParcel(source, null);
            }

            @Override
            public SavedState createFromParcel(final Parcel source, @Nullable final ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            viewHolderSavedState = in.readParcelable(loader);
        }
    }
}
