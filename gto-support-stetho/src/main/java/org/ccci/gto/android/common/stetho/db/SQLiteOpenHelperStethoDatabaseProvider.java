package org.ccci.gto.android.common.stetho.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.NonNull;

import com.facebook.stetho.inspector.database.DatabaseConnectionProvider;
import com.facebook.stetho.inspector.database.DatabaseFilesProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteOpenHelperStethoDatabaseProvider implements DatabaseFilesProvider, DatabaseConnectionProvider {
    private final List<File> mFiles;
    private final Map<File, SQLiteOpenHelper> mDatabases;

    public SQLiteOpenHelperStethoDatabaseProvider(@NonNull final SQLiteOpenHelper... helpers) {
        // create an indexed map of databases
        mDatabases = new HashMap<>(helpers.length);
        for (final SQLiteOpenHelper helper : helpers) {
            final File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                file = new File(helper.getDatabaseName());
            } else {
                file = new File(helper.getClass().toString());
            }

            mDatabases.put(file, helper);
        }

        // generate sorted list of databases
        mFiles = new ArrayList<>(mDatabases.keySet());
        Collections.sort(mFiles);
    }

    @Override
    public List<File> getDatabaseFiles() {
        return mFiles;
    }

    @Override
    public SQLiteDatabase openDatabase(File file) throws SQLiteException {
        return mDatabases.get(file).getWritableDatabase();
    }
}
