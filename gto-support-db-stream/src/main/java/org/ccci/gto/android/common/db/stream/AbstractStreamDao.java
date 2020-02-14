package org.ccci.gto.android.common.db.stream;

import android.database.sqlite.SQLiteOpenHelper;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.StreamDao;

import androidx.annotation.NonNull;

public abstract class AbstractStreamDao extends AbstractDao implements StreamDao {
    protected AbstractStreamDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }
}
