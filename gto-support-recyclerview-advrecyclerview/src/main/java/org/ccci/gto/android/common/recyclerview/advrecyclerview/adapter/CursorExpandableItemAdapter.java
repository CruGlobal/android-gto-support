package org.ccci.gto.android.common.recyclerview.advrecyclerview.adapter;

import android.database.Cursor;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public abstract class CursorExpandableItemAdapter<GVH extends ViewHolder, CVH extends ViewHolder>
        extends AbstractExpandableItemAdapter<GVH, CVH> {
    @Nullable
    protected Cursor mCursor;
    private int mIdColumnIndex = -1;
    @NonNull
    private Group[] mGroupIndex = new Group[0];

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
    @UiThread
    public Cursor swapCursor(@Nullable final Cursor cursor) {
        if (cursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;

        // process the cursor
        int groupIdColumnIndex = -1;
        int idColumnIndex = -1;
        if (cursor != null) {
            groupIdColumnIndex = cursor.getColumnIndexOrThrow(mGroupIdColumn);
            idColumnIndex = cursor.getColumnIndexOrThrow(mIdColumn);
        }

        // store the new index and cursor
        mGroupIndex = buildGroupIndex(cursor, groupIdColumnIndex, idColumnIndex);
        mCursor = cursor;
        mIdColumnIndex = idColumnIndex;

        // notify the RecyclerView that the attached data has changed
        notifyDataSetChanged();

        // return the previous cursor
        return oldCursor;
    }

    @NonNull
    protected Group[] buildGroupIndex(@Nullable final Cursor cursor, final int groupIdColumnIndex,
                                      final int idColumnIndex) {
        // short-circuit if we don't have a Cursor
        if (cursor == null) {
            return new Group[0];
        }

        // extract an index of groups
        final List<Group> groups = new ArrayList<>();
        cursor.moveToPosition(-1);
        Group group = null;
        for (int i = 0; cursor.moveToNext(); i++) {
            // get the id of the current group
            final long groupId = groupIdColumnIndex != -1 ? cursor.getLong(groupIdColumnIndex) : groups.size() + 1;

            // create a new group object if necessary
            if (group == null || group.id != groupId) {
                if (group != null) {
                    groups.add(group);
                }

                group = new Group();
                group.id = groupId;
                group.beginIndex = i;
            }

            // increase the size of this group
            group.size++;
        }

        // make sure we include the final group
        if (group != null) {
            groups.add(group);
        }

        return groups.toArray(new Group[groups.size()]);
    }

    @Override
    public int getGroupCount() {
        return mGroupIndex.length;
    }

    @Override
    public int getChildCount(final int groupPosition) {
        return mGroupIndex[groupPosition].size;
    }

    @Override
    public final long getGroupId(final int groupPosition) {
        return mGroupIndex[groupPosition].id;
    }

    @Override
    public final long getChildId(final int groupPosition, final int childPosition) {
        return scrollCursor(groupPosition, childPosition).getLong(mIdColumnIndex);
    }

    @Override
    public int getGroupItemViewType(final int groupPosition) {
        return getGroupItemViewType(scrollCursor(groupPosition), groupPosition);
    }

    protected int getGroupItemViewType(@NonNull final Cursor c, final int groupPosition) {
        return 0;
    }

    @Override
    public void onBindGroupViewHolder(@NonNull final GVH holder, final int groupPosition, final int viewType) {
        onBindGroupViewHolder(holder, scrollCursor(groupPosition), groupPosition, viewType);
    }

    protected abstract void onBindGroupViewHolder(@NonNull GVH holder, @NonNull Cursor c, int groupPosition,
                                                  int viewType);

    @Override
    public int getChildItemViewType(final int groupPosition, final int childPosition) {
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
        mCursor.moveToPosition(mGroupIndex[groupPosition].beginIndex);
        return mCursor;
    }

    @NonNull
    protected Cursor scrollCursor(final int groupPosition, final int childPosition) {
        assert mCursor != null;
        mCursor.moveToPosition(mGroupIndex[groupPosition].beginIndex + childPosition);
        return mCursor;
    }

    public static class Group {
        public long id = NO_ID;
        public int beginIndex = 0;
        public int size = 0;
    }
}
