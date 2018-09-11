package org.ccci.gto.android.common.stetho.db;

import android.arch.persistence.db.framework.FrameworkSQLiteUtils;
import android.arch.persistence.room.RoomDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RoomStethoDatabaseProvider extends BaseStethoDatabaseProvider {
    private final List<File> mFiles;
    private final Map<File, RoomDatabase> mDatabases;

    public RoomStethoDatabaseProvider(@NonNull final RoomDatabase... databases) {
        // create an indexed map of databases
        mDatabases = new HashMap<>(databases.length);
        for (final RoomDatabase database : databases) {
            final File file = new File(database.getOpenHelper().getDatabaseName());;
            mDatabases.put(file, database);
        }

        // generate sorted list of databases
        mFiles = new ArrayList<>(mDatabases.keySet());
        Collections.sort(mFiles);
    }

    @Override
    public SQLiteDatabase openDatabase(final File file) throws SQLiteException {
        final RoomDatabase db = mDatabases.get(file);
        return db != null ? FrameworkSQLiteUtils.getFrameworkDb(db.getOpenHelper().getWritableDatabase()) : null;
    }

    @Override
    public List<File> getDatabaseFiles() {
        return mFiles;
    }
}
