package org.ccci.gto.android.common.recyclerview.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

public abstract class CursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    @Nullable
    protected Cursor mCursor;
    private int mIdColumn = -1;

    public CursorAdapter() {
        // default to stable ids for CursorAdapters
        setHasStableIds(true);
    }

    @UiThread
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

    @UiThread
    protected Cursor scrollCursor(@Nullable final Cursor cursor, final int position) {
        if (cursor != null) {
            cursor.moveToPosition(position);
        }
        return cursor;
    }

    @UiThread
    @Override
    public long getItemId(final int position) {
        // return the item id if we have a cursor and id column
        if (mIdColumn >= 0) {
            final Cursor c = scrollCursor(mCursor, position);
            if (c != null) {
                return c.getLong(mIdColumn);
            }
        }

        // default to NO_ID
        return RecyclerView.NO_ID;
    }

    @UiThread
    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @UiThread
    @Override
    public final void onBindViewHolder(@NonNull final VH holder, final int position) {
        onBindViewHolder(holder, scrollCursor(mCursor, position), position);
    }

    @UiThread
    protected abstract void onBindViewHolder(@NonNull VH holder, @Nullable Cursor cursor, int position);
}
