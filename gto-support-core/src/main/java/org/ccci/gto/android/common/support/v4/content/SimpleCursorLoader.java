package org.ccci.gto.android.common.support.v4.content;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.loader.content.CursorLoader;

public abstract class SimpleCursorLoader extends CursorLoader {
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
