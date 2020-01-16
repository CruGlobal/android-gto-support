package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import androidx.annotation.RestrictTo
import androidx.core.util.Pools.SynchronizedPool
import java.io.Closeable

private const val STATE_INIT = 0
private const val STATE_OPEN = 1
private const val STATE_SUCCESSFUL = 2
private const val STATE_CLOSED = 3
private const val STATE_RECYCLED = 4

private val POOL = SynchronizedPool<Transaction>(10)

/**
 * Not Thread-safe, this object is designed to be used on a single-thread only
 */
class Transaction private constructor(private var db: SQLiteDatabase?) : Closeable {
    private var state = STATE_INIT

    fun begin() = beginTransaction(true)
    fun beginTransactionNonExclusive() = beginTransaction(false)
    @JvmOverloads
    fun beginTransaction(exclusive: Boolean = true): Transaction {
        if (state < STATE_OPEN) {
            db?.run { if (exclusive) beginTransaction() else beginTransactionNonExclusive() }
            state = STATE_OPEN
        }
        return this
    }

    fun setSuccessful() = setTransactionSuccessful()
    fun setTransactionSuccessful(): Transaction {
        if (state in STATE_OPEN until STATE_SUCCESSFUL) {
            db?.setTransactionSuccessful()
            state = STATE_SUCCESSFUL
        }
        return this
    }

    fun end() = endTransaction()
    fun endTransaction(): Transaction {
        if (state in STATE_OPEN until STATE_CLOSED) {
            db?.endTransaction()
            state = STATE_CLOSED
        }
        return this
    }

    fun recycle() {
        state = STATE_RECYCLED
        db = null
        POOL.release(this)
    }

    override fun close() {
        endTransaction()
    }

    companion object {
        @JvmStatic
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        fun newTransaction(db: SQLiteDatabase) = POOL.acquire()?.also {
            it.db = db
            it.state = STATE_INIT
        } ?: Transaction(db)
    }
}
