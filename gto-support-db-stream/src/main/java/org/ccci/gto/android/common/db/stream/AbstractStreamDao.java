package org.ccci.gto.android.common.db.stream;

import android.database.sqlite.SQLiteOpenHelper;

import com.annimon.stream.Stream;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Query;
import org.ccci.gto.android.common.db.StreamDao;

import androidx.annotation.NonNull;

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
