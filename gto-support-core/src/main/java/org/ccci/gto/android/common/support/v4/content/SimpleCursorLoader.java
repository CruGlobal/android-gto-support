package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public abstract class SimpleCursorLoader extends android.support.v4.content.CursorLoader {
    public SimpleCursorLoader(@NonNull final Context context) {
        super(context);
    }

    @Nullable
    @Override
    public final Cursor loadInBackground() {
        final Cursor c = getCursor();
        if (c != null) {
            // Ensure the cursor window is filled.
            c.getCount();
        }
        return c;
    }

    @Nullable
    @WorkerThread
    protected abstract Cursor getCursor();
}
