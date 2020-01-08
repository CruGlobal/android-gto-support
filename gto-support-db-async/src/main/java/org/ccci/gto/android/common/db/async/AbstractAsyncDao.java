package org.ccci.gto.android.common.db.async;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.db.Expression;
import org.ccci.gto.android.common.db.Query;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AbstractAsyncDao extends AbstractDao {
    protected AbstractAsyncDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }

    @NonNull
    public final <T> ListenableFuture<List<T>> getAsync(@NonNull final Query<T> query) {
        final SettableFuture<List<T>> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(get(query));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final ListenableFuture<Cursor> getCursorAsync(@NonNull final Query<?> query) {
        final SettableFuture<Cursor> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(getCursor(query));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final <T> ListenableFuture<T> findAsync(@NonNull final Class<T> clazz, @NonNull final Object... key) {
        final SettableFuture<T> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(find(clazz, key));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final ListenableFuture<Long> insertAsync(@NonNull final Object obj) {
        return insertAsync(obj, SQLiteDatabase.CONFLICT_NONE);
    }

    @NonNull
    public final ListenableFuture<Long> insertAsync(@NonNull final Object obj, final int conflictAlgorithm) {
        final SettableFuture<Long> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(insert(obj, conflictAlgorithm));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final ListenableFuture<Integer> updateAsync(@NonNull final Object obj) {
        return updateAsync(obj, getFullProjection(obj.getClass()));
    }

    @NonNull
    public final ListenableFuture<Integer> updateAsync(@NonNull final Object obj, @NonNull final String... projection) {
        final SettableFuture<Integer> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(update(obj, projection));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final <T> ListenableFuture<Integer> updateAsync(@NonNull final T sample, @Nullable final Expression where,
                                                           @NonNull final String... projection) {
        final SettableFuture<Integer> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                future.set(update(sample, where, projection));
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }

    @NonNull
    public final ListenableFuture<?> updateOrInsertAsync(@NonNull final Object obj) {
        return updateOrInsertAsync(obj, getFullProjection(obj.getClass()));
    }

    @NonNull
    public final ListenableFuture<?> updateOrInsertAsync(@NonNull final Object obj,
                                                         @NonNull final String... projection) {
        final SettableFuture<Long> future = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            try {
                updateOrInsert(obj, projection);
                future.set(null);
            } catch (final Throwable t) {
                future.setException(t);
            }
        });
        return future;
    }
}
