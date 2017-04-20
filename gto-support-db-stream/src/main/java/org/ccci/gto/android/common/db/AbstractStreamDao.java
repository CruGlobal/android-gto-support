package org.ccci.gto.android.common.db;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

public abstract class AbstractStreamDao extends AbstractDao implements StreamDao {
    protected AbstractStreamDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }

    @NonNull
    @Override
    public <T> Stream<T> streamCompat(@NonNull final Query<T> query) {
        return StreamHelper.stream(this, query);
    }
}
