package org.ccci.gto.android.common.recyclerview.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public abstract class CursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    @Nullable
    protected Cursor mCursor;
    private int mIdColumn = -1;

    public CursorAdapter() {
        // default to stable ids for CursorAdapters
        setHasStableIds(true);
    }

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
        mIdColumn = mCursor != null ? mCursor.getColumnIndex(BaseColumns._ID) : -1;

        // notify that data has changed
        notifyDataSetChanged();

        // return the old cursor
        return old;
    }

    @Override
    public long getItemId(final int position) {
        // return the item id if we have a cursor and id column
        if (mCursor != null && mIdColumn >= 0) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mIdColumn);
        }

        // default to NO_ID
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }
}
