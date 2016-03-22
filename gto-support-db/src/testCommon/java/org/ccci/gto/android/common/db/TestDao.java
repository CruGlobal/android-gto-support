package org.ccci.gto.android.common.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.ccci.gto.android.common.db.Contract.RootTable;
import org.ccci.gto.android.common.db.model.Root;

public class TestDao extends AbstractDao {
    private TestDao(@Nullable final Context context) {
        //noinspection ConstantConditions
        super(context != null ? TestDatabase.getInstance(context) : null);
        registerType(Root.class, RootTable.TABLE_NAME, RootTable.PROJECTION_ALL, new RootMapper(),
                     RootTable.SQL_WHERE_PRIMARY_KEY);
    }

    private static TestDao INSTANCE;
    static TestDao getInstance(@NonNull final Context context) {
        synchronized (TestDao.class) {
            if (INSTANCE == null) {
                INSTANCE = new TestDao(context.getApplicationContext());
            }
            return INSTANCE;
        }
    }

    static TestDao mock() {
        return new TestDao(null);
    }

    @NonNull
    @Override
    protected Expression getPrimaryKeyWhere(@NonNull final Object obj) {
        if(obj instanceof Root) {
            return getPrimaryKeyWhere(Root.class, ((Root) obj).id);
        }
        return super.getPrimaryKeyWhere(obj);
    }
}
