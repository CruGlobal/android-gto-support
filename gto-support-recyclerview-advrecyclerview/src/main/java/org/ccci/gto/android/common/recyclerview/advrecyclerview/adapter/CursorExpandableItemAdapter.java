package org.ccci.gto.android.common.recyclerview.advrecyclerview.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class CursorExpandableItemAdapter<GVH extends ViewHolder, CVH extends ViewHolder>
        extends AbstractExpandableItemAdapter<GVH, CVH> {
    @Nullable
    protected Cursor mCursor;
    private int mGroupIdColumnIndex = -1;
    private int mIdColumnIndex = -1;
    @NonNull
    private Integer[] mIndex = new Integer[0];

    @NonNull
    private String mIdColumn = BaseColumns._ID;
    @NonNull
    private String mGroupIdColumn = BaseColumns._ID;

    public void setGroupIdColumn(@NonNull final String column) {
        mGroupIdColumn = column;
    }

    public void setIdColumn(@NonNull final String idColumn) {
        mIdColumn = idColumn;
    }

    @Nullable
    public Cursor swapCursor(@Nullable final Cursor cursor) {
        if (cursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;

        // create index of cursor
        final List<Integer> index = new ArrayList<>();

        // process the cursor
        int groupIdColumnIndex = -1;
        int idColumnIndex = -1;
        if (cursor != null) {
            groupIdColumnIndex = cursor.getColumnIndexOrThrow(mGroupIdColumn);
            idColumnIndex = cursor.getColumnIndexOrThrow(mIdColumn);

            // extract the start indices for unique groups
            cursor.moveToPosition(-1);
            long currentGroup = 0;
            for (int i = 0; cursor.moveToNext(); i++) {
                final long group = groupIdColumnIndex != -1 ? cursor.getLong(groupIdColumnIndex) : currentGroup + 1;
                if (cursor.isFirst() || currentGroup != group) {
                    index.add(i);
                    currentGroup = group;
                }
            }
        }

        // store the new index and cursor
        mIndex = index.toArray(new Integer[index.size()]);
        mCursor = cursor;
        mGroupIdColumnIndex = groupIdColumnIndex;
        mIdColumnIndex = idColumnIndex;

        // notify the RecyclerView that the attached data has changed
        notifyDataSetChanged();

        // return the previous cursor
        return oldCursor;
    }

    @Override
    public int getGroupCount() {
        return mIndex.length;
    }

    @Override
    public int getChildCount(final int groupPosition) {
        assert mCursor != null;
        final int start = mIndex[groupPosition];
        final int end = groupPosition + 1 < mIndex.length ? mIndex[groupPosition + 1] : mCursor.getCount();
        return end - start;
    }

    @Override
    public final long getGroupId(final int groupPosition) {
        return scrollCursor(groupPosition).getLong(mGroupIdColumnIndex);
    }

    @Override
    public final long getChildId(final int groupPosition, final int childPosition) {
        return scrollCursor(groupPosition, childPosition).getLong(mIdColumnIndex);
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return getGroupItemViewType(scrollCursor(groupPosition), groupPosition);
    }

    protected int getGroupItemViewType(@NonNull final Cursor c, final int groupPosition) {
        return 0;
    }

    @Override
    public final void onBindGroupViewHolder(@NonNull final GVH holder, final int groupPosition, final int viewType) {
        onBindGroupViewHolder(holder, scrollCursor(groupPosition), groupPosition, viewType);
    }

    protected abstract void onBindGroupViewHolder(@NonNull GVH holder, @NonNull Cursor c, int groupPosition,
                                                  int viewType);

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return getChildItemViewType(scrollCursor(groupPosition, childPosition), groupPosition, childPosition);
    }

    protected int getChildItemViewType(@NonNull final Cursor c, final int groupPosition, final int childPosition) {
        return 0;
    }

    @Override
    public final void onBindChildViewHolder(@NonNull final CVH holder, final int groupPosition, final int childPosition,
                                            final int viewType) {
        onBindChildViewHolder(holder, scrollCursor(groupPosition, childPosition), groupPosition, childPosition,
                              viewType);
    }

    protected abstract void onBindChildViewHolder(@NonNull CVH holder, @NonNull Cursor c, int groupPosition,
                                                  int childPosition, int viewType);

    @Override
    public final boolean onCheckCanExpandOrCollapseGroup(@NonNull final GVH holder, final int groupPosition,
                                                         final int x, final int y, final boolean expand) {
        return onCheckCanExpandOrCollapseGroup(holder, scrollCursor(groupPosition), groupPosition, x, y, expand);
    }

    protected abstract boolean onCheckCanExpandOrCollapseGroup(@NonNull GVH holder, @NonNull Cursor c,
                                                               int groupPosition, int x, int y, boolean expand);

    @NonNull
    protected Cursor scrollCursor(final int groupPosition) {
        assert mCursor != null;
        mCursor.moveToPosition(mIndex[groupPosition]);
        return mCursor;
    }

    @NonNull
    protected Cursor scrollCursor(final int groupPosition, final int childPosition) {
        assert mCursor != null;
        mCursor.moveToPosition(mIndex[groupPosition] + childPosition);
        return mCursor;
    }
}
