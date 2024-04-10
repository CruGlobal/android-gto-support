package org.ccci.gto.android.common.db

import com.annimon.stream.Stream

/**
 * Stream DAO interface relying on backwards compatible Lightweight Stream API.
 */
@Deprecated("Since v4.2.0, apps should use Room instead of our custom DB solution")
interface StreamDao : Dao {
    fun <T : Any> streamCompat(query: Query<T>): Stream<T> = Stream.of(get(query))
}
