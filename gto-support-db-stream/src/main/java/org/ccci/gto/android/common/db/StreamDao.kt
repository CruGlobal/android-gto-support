package org.ccci.gto.android.common.db

import com.annimon.stream.Stream

/**
 * Stream DAO interface relying on backwards compatible Lightweight Stream API.
 */
interface StreamDao : Dao {
    fun <T : Any> streamCompat(query: Query<T>): Stream<T> = Stream.of(get(query))
}
