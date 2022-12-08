package org.ccci.gto.android.common.androidx.room;

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
import kotlinx.coroutines.ExecutorsKt;

// TODO: convert this to Kotlin once Android Test Fixtures support Kotlin
//       https://youtrack.jetbrains.com/issue/KT-50667
//       https://issuetracker.google.com/issues/259523353
public class RoomDatabaseRule<T extends RoomDatabase> extends TestWatcher {
    public RoomDatabaseRule(@NonNull Class<T> dbClass) {
        this(dbClass, (Executor) null);
    }

    public RoomDatabaseRule(@NonNull Class<T> dbClass, @Nullable CoroutineDispatcher queryDispatcher) {
        this(dbClass, queryDispatcher != null ? ExecutorsKt.asExecutor(queryDispatcher) : null);
    }

    public RoomDatabaseRule(@NonNull Class<T> dbClass, @Nullable Executor queryExecutor) {
        mDbClass = dbClass;
        mQueryExecutor = queryExecutor;
    }

    @NonNull
    private final Class<T> mDbClass;
    @Nullable
    private final Executor mQueryExecutor;

    @Nullable
    private T mDb;

    @NonNull
    public T getDb() {
        if (mDb == null) throw new IllegalStateException();
        return mDb;
    }

    @Override
    protected void starting(Description description) {
        mDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), mDbClass)
                .allowMainThreadQueries()
                .setQueryExecutor(mQueryExecutor != null ? mQueryExecutor : ArchTaskExecutor.getIOThreadExecutor())
                .setTransactionExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .build();
    }

    @Override
    protected void finished(Description description) {
        if (mDb != null) mDb.close();
        mDb = null;
    }
}
