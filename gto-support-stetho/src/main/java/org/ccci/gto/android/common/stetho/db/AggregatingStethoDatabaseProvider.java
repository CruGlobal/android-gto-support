package org.ccci.gto.android.common.stetho.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.facebook.stetho.inspector.database.DatabaseConnectionProvider;
import com.facebook.stetho.inspector.database.DatabaseFilesProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AggregatingStethoDatabaseProvider extends BaseStethoDatabaseProvider {
    private final List<? extends DatabaseFilesProvider> mFilesProviders;
    private final List<? extends DatabaseConnectionProvider> mConnectionProviders;

    public <T extends DatabaseFilesProvider & DatabaseConnectionProvider> AggregatingStethoDatabaseProvider(
            final T... providers) {
        final List<T> list = Arrays.asList(providers);
        mFilesProviders = list;
        mConnectionProviders = list;
    }

    @Override
    public List<File> getDatabaseFiles() {
        final List<File> files = new ArrayList<>();
        for (final DatabaseFilesProvider provider : mFilesProviders) {
            files.addAll(provider.getDatabaseFiles());
        }
        return files;
    }

    @Override
    public SQLiteDatabase openDatabase(final File databaseFile) throws SQLiteException {
        for (final DatabaseConnectionProvider provider : mConnectionProviders) {
            final SQLiteDatabase db = provider.openDatabase(databaseFile);
            if (db != null) {
                return db;
            }
        }
        return null;
    }
}
