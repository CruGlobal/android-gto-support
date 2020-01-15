package org.ccci.gto.android.common.db;

import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public abstract class AbstractDao extends AbstractDao2 {
    protected AbstractDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }
}
