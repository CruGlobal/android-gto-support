package org.ccci.gto.android.common.androidx.room;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.test.core.app.ApplicationProvider;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.concurrent.Executor;

import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.ExecutorsKt;

// TODO: convert this to Kotlin once Android Test Fixtures support Kotlin
//       https://youtrack.jetbrains.com/issue/KT-50667
//       https://issuetracker.google.com/issues/259523353
public class RoomDatabaseRule<T extends RoomDatabase> extends TestWatcher {
    public RoomDatabaseRule(@NonNull Class<T> dbClass) {
        this(dbClass, (Executor) null, null);
    }

    public RoomDatabaseRule(@NonNull Class<T> dbClass, @Nullable CoroutineDispatcher queryDispatcher) {
        this(dbClass, queryDispatcher, null);
    }

    public RoomDatabaseRule(@NonNull Class<T> dbClass, @Nullable Executor queryExecutor) {
        this(dbClass, queryExecutor, null);
    }

    public RoomDatabaseRule(
            @NonNull Class<T> dbClass,
            @Nullable CoroutineDispatcher queryDispatcher,
            @Nullable CoroutineScope coroutineScope
    ) {
        this(dbClass, queryDispatcher != null ? ExecutorsKt.asExecutor(queryDispatcher) : null, coroutineScope);
    }

    public RoomDatabaseRule(
            @NonNull Class<T> dbClass,
            @Nullable Executor queryExecutor,
            @Nullable CoroutineScope coroutineScope
    ) {
        mDbClass = dbClass;
        mQueryExecutor = queryExecutor;
        mCoroutineScope = coroutineScope;
    }

    @NonNull
    private final Class<T> mDbClass;
    @Nullable
    private final Executor mQueryExecutor;
    @Nullable
    private final CoroutineScope mCoroutineScope;

    @Nullable
    private T mDb;

    @NonNull
    public T getDb() {
        if (mDb == null) throw new IllegalStateException();
        return mDb;
    }

    @Override
    @SuppressLint("RestrictedApi")
    protected void starting(Description description) {
        mDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), mDbClass)
                .allowMainThreadQueries()
                .setQueryExecutor(mQueryExecutor != null ? mQueryExecutor : ArchTaskExecutor.getIOThreadExecutor())
                .setTransactionExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .build();
        if (mCoroutineScope != null) {
            TestRoomDatabaseCoroutines.setCoroutineScope(mDb, mCoroutineScope);
        }
    }

    @Override
    protected void finished(Description description) {
        if (mDb != null) mDb.close();
        mDb = null;
    }
}
