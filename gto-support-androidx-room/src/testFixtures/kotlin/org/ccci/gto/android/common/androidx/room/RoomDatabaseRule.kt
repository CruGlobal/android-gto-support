package org.ccci.gto.android.common.androidx.room

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RoomDatabaseRule<T : RoomDatabase>(
    private val dbClass: Class<T>,
    private val queryExecutor: Executor? = null,
    private val coroutineScope: CoroutineScope? = null
) : TestWatcher() {
    constructor(
        dbClass: Class<T>,
        queryDispatcher: CoroutineDispatcher,
        coroutineScope: CoroutineScope? = null
    ) : this(dbClass, queryDispatcher.asExecutor(), coroutineScope)

    private var _db: T? = null
    val db: T get() = checkNotNull(_db)

    @SuppressLint("RestrictedApi")
    override fun starting(description: Description) {
        _db = inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), dbClass)
            .allowMainThreadQueries()
            .setQueryExecutor(queryExecutor ?: ArchTaskExecutor.getIOThreadExecutor())
            .setTransactionExecutor(ArchTaskExecutor.getIOThreadExecutor())
            .build()
        if (coroutineScope != null) {
            TestRoomDatabaseCoroutines.setCoroutineScope(db, coroutineScope)
        }
    }

    override fun finished(description: Description) {
        _db?.close()
        _db = null
    }
}
