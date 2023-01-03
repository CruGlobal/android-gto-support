package org.ccci.gto.android.common.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteTransactionListener
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
internal class Transaction private constructor(
    private var db: SQLiteDatabase?,
    internal var transactionListener: SQLiteTransactionListener? = null,
) : Closeable {
    private var state = STATE_INIT

    fun beginTransaction(exclusive: Boolean = true): Transaction {
        if (state < STATE_OPEN) {
            when {
                exclusive -> db!!.beginTransactionWithListener(transactionListener)
                else -> db!!.beginTransactionWithListenerNonExclusive(transactionListener)
            }
            state = STATE_OPEN
        }
        return this
    }

    fun setTransactionSuccessful(): Transaction {
        if (state in STATE_OPEN until STATE_SUCCESSFUL) {
            db!!.setTransactionSuccessful()
            state = STATE_SUCCESSFUL
        }
        return this
    }

    fun endTransaction(): Transaction {
        if (state in STATE_OPEN until STATE_CLOSED) {
            try {
                db!!.endTransaction()
            } finally {
                (transactionListener as? Listener)?.onFinished()
            }
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

    internal interface Listener {
        fun onFinished()
    }

    companion object {
        @JvmStatic
        fun newTransaction(db: SQLiteDatabase, transactionListener: SQLiteTransactionListener? = null) =
            POOL.acquire()?.also {
                it.db = db
                it.transactionListener = transactionListener
                it.state = STATE_INIT
            } ?: Transaction(db, transactionListener)
    }
}
