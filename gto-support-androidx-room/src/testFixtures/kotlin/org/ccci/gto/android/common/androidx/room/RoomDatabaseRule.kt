package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RoomDatabaseRule<T : RoomDatabase>(
    private val dbClass: Class<T>,
    private val coroutineContext: CoroutineContext? = null,
    private val queryExecutor: Executor? = null,
) : TestWatcher() {
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
