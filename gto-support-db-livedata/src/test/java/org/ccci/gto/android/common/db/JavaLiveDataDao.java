package org.ccci.gto.android.common.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

public final class JavaLiveDataDao extends AbstractDao implements LiveDataDao {
    protected JavaLiveDataDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }

    private final LiveDataRegistry mLiveDataRegistry = new LiveDataRegistry();

    @NotNull
    @Override
    public LiveDataRegistry getLiveDataRegistry() {
        return mLiveDataRegistry;
    }

    private void insertMethods() {
        // This makes sure method signatures for java don't change
        insert(new Object());
        insert(new Object(), SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void updateMethods() {
        // This makes sure method signatures for java don't change
        update(new Object(), Expression.NULL, "a");
        update(new Object(), Expression.NULL, SQLiteDatabase.CONFLICT_IGNORE, "a");
    }
}
