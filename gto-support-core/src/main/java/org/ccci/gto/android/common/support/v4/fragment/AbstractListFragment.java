package org.ccci.gto.android.common.support.v4.fragment;

import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import androidx.fragment.app.ListFragment;

/**
 * @deprecated Since v3.7.0, we no longer use a ListFragment and Cursors anywhere, so remove this dead code.
 */
@Deprecated
public class AbstractListFragment extends ListFragment {
    protected void changeCursor(final Cursor cursor) {
        final Cursor old = this.swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    protected Cursor swapCursor(final Cursor cursor) {
        final ListAdapter adapter = getListAdapter();
        if (adapter instanceof CursorAdapter) {
            return ((CursorAdapter) adapter).swapCursor(cursor);
        } else if (adapter instanceof androidx.cursoradapter.widget.CursorAdapter) {
            return ((androidx.cursoradapter.widget.CursorAdapter) adapter).swapCursor(cursor);
        }

        return null;
    }
}
