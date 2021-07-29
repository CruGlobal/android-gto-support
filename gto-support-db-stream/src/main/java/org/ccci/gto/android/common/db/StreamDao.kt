package org.ccci.gto.android.common.db

import com.annimon.stream.Stream

/**
 * Stream DAO interface relying on backwards compatible Lightweight Stream API.
 */
interface StreamDao : Dao {
    fun <T : Any> streamCompat(query: Query<T>): Stream<T> = Stream.of(get(query))

    object StreamHelper {
        @JvmStatic
        @Deprecated("Since v3.4.0, use default implementation of streamCompat instead.")
        fun <T : Any> stream(dao: Dao, query: Query<T>): Stream<T> = Stream.of(dao.get(query))
    }
}
