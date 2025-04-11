package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RoomDatabaseRule<T : RoomDatabase>(
    private val dbClass: Class<T>,
    private val coroutineContext: CoroutineContext? = null,
    private val queryExecutor: Executor? = null,
) : TestWatcher() {
    @Deprecated("Since v4.4.0, pass a coroutineContext directly instead.")
    constructor(dbClass: Class<T>, queryDispatcher: CoroutineDispatcher, coroutineScope: CoroutineScope) :
        this(dbClass, coroutineScope.coroutineContext + queryDispatcher)

    @Deprecated(
        "Since v4.4.0, passing both a coroutineContext and a queryExecutor is not supported.",
        level = DeprecationLevel.ERROR
    )
    constructor(dbClass: Class<T>, queryExecutor: Executor, coroutineScope: CoroutineScope) :
        this(dbClass, coroutineScope.coroutineContext, queryExecutor)

    private var _db: T? = null
    val db: T get() = checkNotNull(_db)

    @SuppressLint("RestrictedApi")
    override fun starting(description: Description) {
        _db = inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), dbClass)
            .allowMainThreadQueries()
            .apply {
                if (coroutineContext != null) setQueryCoroutineContext(coroutineContext)
                if (queryExecutor != null) {
                    setQueryExecutor(queryExecutor)
                    setTransactionExecutor(queryExecutor)
                }
            }
            .build()
    }

    override fun finished(description: Description) {
        _db?.close()
        _db = null
    }
}
