package org.ccci.gto.android.common.adapter;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LongSparseArray;

public abstract class CursorFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String ARG_ID = CursorFragmentStatePagerAdapter.class.getName() + ".ARG_ID";

    private Cursor c = null;
    private int idColumn = -1;
    private LongSparseArray<Integer> idIndex;

    protected CursorFragmentStatePagerAdapter(final FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return c != null ? c.getCount() : 0;
    }

    @Override
    public final Fragment getItem(final int position) {
        if (this.c != null) {
            this.c.moveToPosition(position);
            if (!this.c.isBeforeFirst() && !this.c.isAfterLast()) {
                final Fragment fragment = this.getFragment(this.c);
                if (fragment != null) {
                    // track the id in the fragment arguments
                    if (this.idColumn != -1) {
                        Bundle args = fragment.getArguments();
                        if (args == null) {
                            args = new Bundle();
                        }
                        args.putLong(ARG_ID, c.getInt(this.idColumn));
                        fragment.setArguments(args);
                    }

                    return fragment;
                }
            }
        }

        return null;
    }

    protected abstract Fragment getFragment(Cursor c);

    @Override
    public int getItemPosition(final Object object) {
        final Fragment fragment = (Fragment) object;
        final Bundle args;
        if (this.c != null && this.idColumn != -1 && (args = fragment.getArguments()) != null &&
                args.containsKey(ARG_ID)) {
            // find the position based on the id
            this.generateIdIndex();
            final Integer position = this.idIndex.get(args.getLong(ARG_ID, -1));
            if (position != null) {
                return position;
            }
        }

        // default to not being present
        return POSITION_NONE;
    }

    public final Cursor swapCursor(final Cursor c) {
        final Cursor old = this.c;

        // update cursor
        this.c = c;
        if (this.c != null) {
            this.idColumn = this.c.getColumnIndex(BaseColumns._ID);
        } else {
            this.idColumn = -1;
        }
        this.idIndex = null;

        // trigger a cursor update event
        this.onCursorUpdate(this.c);

        this.notifyDataSetChanged();
        return old;
    }

    protected void onCursorUpdate(final Cursor c) {
    }

    private void generateIdIndex() {
        if (this.idIndex == null) {
            if (this.c != null && this.idColumn != -1) {
                this.idIndex = new LongSparseArray<Integer>(this.c.getCount());

                for (int position = 0; position < this.c.getCount(); position++) {
                    this.c.moveToPosition(position);
                    this.idIndex.put(this.c.getLong(this.idColumn), position);
                }
            } else {
                this.idIndex = new LongSparseArray<Integer>();
            }
        }
    }
}
