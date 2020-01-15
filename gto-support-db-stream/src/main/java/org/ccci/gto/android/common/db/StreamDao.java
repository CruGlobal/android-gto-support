package org.ccci.gto.android.common.db;

import com.annimon.stream.Stream;

import androidx.annotation.NonNull;

/**
 * Stream DAO interface relying on backwards compatible Lightweight Stream API.
 */
public interface StreamDao {
    @NonNull
    <T> Stream<T> streamCompat(@NonNull Query<T> query);

    class StreamHelper {
        @NonNull
        public static <T> Stream<T> stream(@NonNull final Dao dao, @NonNull final Query<T> query) {
            return Stream.of(dao.get(query));
        }
    }
}
