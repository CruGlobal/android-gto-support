package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteTransactionListener
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
@Deprecated("""
    Since v3.3.0, this is now an internal class and not meant to be part of the public API.
    You should transition to utilize Dao.inTransaction() or Dao.transaction {} instead.
    This will be restricted to Library usage only in v3.4.0, and removed from the public API completely in v3.5.0.
    """)
class Transaction private constructor(
    private var db: SQLiteDatabase?,
    internal var transactionListener: SQLiteTransactionListener? = null
) : Closeable {
    private var state = STATE_INIT

    fun begin() = beginTransaction(true)
    fun beginTransactionNonExclusive() = beginTransaction(false)
    @JvmOverloads
    fun beginTransaction(exclusive: Boolean = true): Transaction {
        if (state < STATE_OPEN) {
            db?.run {
                if (exclusive) beginTransactionWithListener(transactionListener)
                else beginTransactionWithListenerNonExclusive(transactionListener)
            }
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
        transactionListener = null
        db = null
        parent = null
        invalidatedClasses.clear()
        POOL.release(this)
    }

    override fun close() {
        endTransaction()
    }

    // region Invalidation Tracking
    internal var parent: Transaction? = null
    internal val invalidatedClasses = mutableSetOf<Class<*>>()
    // endregion Invalidation Tracking

    companion object {
        @JvmStatic
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        fun newTransaction(db: SQLiteDatabase, transactionListener: SQLiteTransactionListener? = null) =
            POOL.acquire()?.also {
                it.db = db
                it.transactionListener = transactionListener
                it.state = STATE_INIT
            } ?: Transaction(db, transactionListener)
    }
}
