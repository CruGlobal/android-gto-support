package org.ccci.gto.android.common.viewpager.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import org.ccci.gto.android.common.viewpager.adapter.ViewHolderPagerAdapter.ViewHolder;

public abstract class CursorPagerAdapter<VH extends ViewHolder> extends ViewHolderPagerAdapter<VH> {
    @Nullable
    private Cursor mCursor = null;
    private int mIdColumn = -1;
    @NonNull
    private LongSparseArray<Integer> mIdIndex = new LongSparseArray<>();

    public CursorPagerAdapter() {
        // default to having stable ids
        setHasStableIds(true);
    }

    /* BEGIN lifecycle */

    protected void onCursorChanged(@Nullable final Cursor old, @Nullable final Cursor c) {
        mIdColumn = mCursor != null ? mCursor.getColumnIndex(BaseColumns._ID) : -1;
        generateIdIndex();
    }

    @Override
    protected void onBindViewHolder(@NonNull final VH holder, final int position) {
        super.onBindViewHolder(holder, position);
        assert mCursor != null : "This is only called for valid positions, which implies we have a valid Cursor";
        mCursor.moveToPosition(position);
        onBindViewHolder(holder, mCursor);
    }

    protected void onBindViewHolder(@NonNull final VH holder, @NonNull final Cursor c) {
    }

    /* END lifecycle */

    public void changeCursor(@Nullable final Cursor cursor) {
        final Cursor old = swapCursor(cursor);

        // close old Cursor if it differs from the new Cursor
        if (old != null && old != cursor) {
            old.close();
        }
    }

    @Nullable
    public Cursor swapCursor(@Nullable final Cursor cursor) {
        final Cursor old = mCursor;

        // update Cursor
        mCursor = cursor;
        onCursorChanged(old, mCursor);

        // notify that data has changed
        notifyDataSetChanged();

        // return the old cursor
        return old;
    }

    private void generateIdIndex() {
        if (mCursor != null && mIdColumn != -1) {
            mIdIndex = new LongSparseArray<>(mCursor.getCount());

            for (int position = 0; position < mCursor.getCount(); position++) {
                mCursor.moveToPosition(position);
                mIdIndex.put(mCursor.getLong(mIdColumn), position);
            }
        } else {
            mIdIndex = new LongSparseArray<>();
        }
    }

    @Override
    protected boolean hasStableIds() {
        return super.hasStableIds() && mIdColumn != -1;
    }

    @Override
    public final int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public final long getItemId(final int position) {
        // return the item id if we have a cursor and id column
        if (mCursor != null && mIdColumn >= 0) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mIdColumn);
        }

        // default to NO_ID
        return NO_ID;
    }

    @Override
    protected final int getItemPositionFromId(final long id) {
        return mIdIndex.get(id, POSITION_NONE);
    }
}
