package org.ccci.gto.android.common.db;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

/**
 * Stream DAO interface relying on backwards compatible Lightweight Stream API.
 */
public interface StreamDao {
    @NonNull
    <T> Stream<T> streamCompat(@NonNull Query<T> query);

    class StreamHelper {
        @NonNull
        public static <T> Stream<T> stream(@NonNull final AbstractDao dao, @NonNull final Query<T> query) {
            return Stream.of(dao.get(query));
        }
    }
}
