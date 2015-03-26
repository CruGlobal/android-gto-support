package org.ccci.gto.android.common.support.v4.fragment;

import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ListFragment;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import org.ccci.gto.android.common.support.v4.util.FragmentUtils;

public class AbstractListFragment extends ListFragment {
    protected final <T> T findView(final Class<T> clazz, final int id) {
        return FragmentUtils.findView(this, clazz, id);
    }

    protected final <T> T getListener(final Class<T> clazz) {
        return FragmentUtils.getListener(this, clazz);
    }

    protected void changeCursor(final Cursor cursor) {
        final Cursor old = this.swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    protected Cursor swapCursor(final Cursor cursor) {
        final ListAdapter adapter = getListAdapter();
        if (adapter instanceof CursorAdapter) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return ((CursorAdapter) adapter).swapCursor(cursor);
            } else {
                ((CursorAdapter) adapter).changeCursor(cursor);
            }
        } else if (adapter instanceof android.support.v4.widget.CursorAdapter) {
            return ((android.support.v4.widget.CursorAdapter) adapter).swapCursor(cursor);
        }

        return null;
    }
}
