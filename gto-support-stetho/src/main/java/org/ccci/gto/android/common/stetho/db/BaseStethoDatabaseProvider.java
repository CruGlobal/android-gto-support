package org.ccci.gto.android.common.stetho.db;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.inspector.database.DatabaseConnectionProvider;
import com.facebook.stetho.inspector.database.DatabaseFilesProvider;
import com.facebook.stetho.inspector.database.SqliteDatabaseDriver;

public abstract class BaseStethoDatabaseProvider implements DatabaseFilesProvider, DatabaseConnectionProvider {
    @NonNull
    public final SqliteDatabaseDriver toDatabaseDriver(@NonNull final Context context) {
        return new SqliteDatabaseDriver(context, this, this);
    }
}
