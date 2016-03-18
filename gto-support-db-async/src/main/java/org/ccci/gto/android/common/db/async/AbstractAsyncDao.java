package org.ccci.gto.android.common.db.async;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.ccci.gto.android.common.db.AbstractDao;
import org.ccci.gto.android.common.util.AsyncTaskCompat;

public abstract class AbstractAsyncDao extends AbstractDao {
    protected AbstractAsyncDao(@NonNull final SQLiteOpenHelper helper) {
        super(helper);
    }

    @NonNull
    public final <T> ListenableFuture<T> findAsync(@NonNull final Class<T> clazz, @NonNull final Object... key) {
        final SettableFuture<T> future = SettableFuture.create();
        AsyncTaskCompat.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    future.set(find(clazz, key));
                } catch (Throwable t) {
                    future.setException(t);
                }
            }
        });
        return future;
    }

    @NonNull
    public final <T> ListenableFuture<?> updateAsync(@NonNull final T obj) {
        return updateAsync(obj, getFullProjection(obj.getClass()));
    }

    @NonNull
    public final <T> ListenableFuture<?> updateAsync(@NonNull final T obj, @NonNull final String... projection) {
        final SettableFuture<?> future = SettableFuture.create();
        AsyncTaskCompat.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    update(obj, projection);
                    future.set(null);
                } catch (Throwable t) {
                    future.setException(t);
                }
            }
        });
        return future;
    }
}
